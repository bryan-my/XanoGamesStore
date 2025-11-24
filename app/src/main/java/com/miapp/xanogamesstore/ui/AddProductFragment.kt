package com.miapp.xanogamesstore.ui import androidx.core.widget.NestedScrollView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.miapp.xanogamesstore.api.UploadResponse
class AddProductFragment : Fragment() {

    // ---- Estado de edición / imágenes existentes ----
    private var editingId: Int? = null
    private var existingImages: List<UploadResponse> = emptyList()

    companion object {
        private const val ARG_EDIT_ID = "edit_id"
        fun edit(id: Int) = AddProductFragment().apply {
            arguments = Bundle().apply { putInt(ARG_EDIT_ID, id) }
        }
    }

    // ---- Vistas ----
    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText
    private lateinit var etBrand: EditText
    private lateinit var etCategory: EditText
    private lateinit var btnPick: Button
    private lateinit var btnSave: Button
    private lateinit var ivPreview: ImageView
    private lateinit var progress: ProgressBar

    /**
     * Lista de URIs de imágenes seleccionadas. Se usa cuando el usuario elige
     * múltiples imágenes a la vez. Siempre se vacía antes de añadir nuevas
     * selecciones para evitar conservar imágenes de selecciones previas.
     */
    private var selectedUris: MutableList<Uri> = mutableListOf()

    /**
     * Lanzador del selector de imágenes. Utiliza la variante
     * {@link ActivityResultContracts.PickMultipleVisualMedia} para permitir
     * seleccionar más de una imagen a la vez. Se limita a un máximo de 5
     */
    private val picker = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        // Limpiamos cualquier selección previa
        selectedUris.clear()
        // Añadimos las nuevas URIs
        selectedUris.addAll(uris ?: emptyList())
        // Si hay al menos una imagen, mostramos la primera como preview
        if (selectedUris.isNotEmpty()) {
            ivPreview.setImageURI(selectedUris.first())
        } else {
            // Si no se seleccionó nada, limpiamos la vista previa
            ivPreview.setImageDrawable(null)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // 1) Inflar y referenciar vistas (¡primero!)
        val v = inflater.inflate(R.layout.fragment_add_product, container, false)
        etName = v.findViewById(R.id.etName)
        etDescription = v.findViewById(R.id.etDescription)
        etPrice = v.findViewById(R.id.etPrice)
        etStock = v.findViewById(R.id.etStock)
        etBrand = v.findViewById(R.id.etBrand)
        etCategory = v.findViewById(R.id.etCategory)
        ivPreview = v.findViewById(R.id.ivPreview)
        btnPick = v.findViewById(R.id.btnPick)
        btnSave = v.findViewById(R.id.btnSave)
        progress = v.findViewById(R.id.progress)

        // 2) Listeners (una sola vez)
        btnPick.setOnClickListener {
            // Lanzamos el selector de múltiples imágenes. Especificamos que
            // únicamente se permitan seleccionar imágenes (no vídeos ni otros
            // tipos de contenido) mediante el parámetro ImageOnly. El valor
            // máximo de imágenes lo definimos en el registro del ActivityResult.
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        btnSave.setOnClickListener { saveProduct() }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 3) Leer argumento y precargar SOLO aquí (la vista ya existe)
        editingId = arguments?.getInt(ARG_EDIT_ID)
        editingId?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val api = ApiClient.shop(requireContext()).create(ProductService::class.java)
                    val p = withContext(Dispatchers.IO) { api.getProduct(id) }

                    etName.setText(p.name)
                    etDescription.setText(p.description)
                    etPrice.setText(p.price.toString())
                    etStock.setText(p.stock.toString())
                    etBrand.setText(p.brand)
                    etCategory.setText(p.category)

                    existingImages = p.image?.mapNotNull { img ->
                        val pth = img.path ?: return@mapNotNull null
                        UploadResponse(
                            path = pth,
                            name = img.name,
                            mime = img.mime,
                            size = img.size,   // Long?
                            type = "image"     // por compatibilidad (si tu /upload no lo trae)
                        )
                    } ?: emptyList()

                    btnSave.text = "Guardar cambios"

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message ?: "Error cargando producto", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun getFileSizeBytes(uri: Uri): Int {
        val cr = requireContext().contentResolver
        cr.query(uri, arrayOf(android.provider.OpenableColumns.SIZE), null, null, null).use { c ->
            if (c != null && c.moveToFirst()) {
                val idx = c.getColumnIndex(android.provider.OpenableColumns.SIZE)
                if (idx >= 0) return (c.getLong(idx)).toInt()
            }
        }
        return 0
    }
    // ---- ÚNICA función de guardado (crear o editar) ----
    private fun saveProduct() {
        val name = etName.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val price = etPrice.text.toString().trim().toDoubleOrNull()
        val stock = etStock.text.toString().trim().toIntOrNull()
        val brand = etBrand.text.toString().trim()
        val category = etCategory.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || price == null || stock == null ||
            brand.isEmpty() || category.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        progress.visibility = View.VISIBLE
        btnSave.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val uploadApi = ApiClient.upload(requireContext()).create(UploadService::class.java)
                val shopApi = ApiClient.shop(requireContext()).create(ProductService::class.java)

                // Si eliges nuevas imágenes -> subirlas todas; si no, usar las existentes (cuando editas)
                var images: List<ImagePayload> = existingImages
                if (selectedUris.isNotEmpty()) {
                    // Subimos cada imagen seleccionada y construimos la lista de UploadResponse
                    val uploadResponses = mutableListOf<UploadResponse>()
                    for (uri in selectedUris) {
                        val (part, _) = uriToContentPart(requireContext().contentResolver, uri)
                        val up = withContext(Dispatchers.IO) { uploadApi.upload(part) }
                        uploadResponses.add(up)
                    }
                    images = uploadResponses
                }

                // Cuerpo compatible con el Input de Xano (evita "Missing param: t..." por title)
                val body = CreateProductBody(
                    name = name,
                    description = description,
                    price = price,
                    stock = stock,
                    brand = brand,
                    category = category,
                    image = images          // List<UploadResponse>
                )
                val result = withContext(Dispatchers.IO) {
                    if (editingId == null) shopApi.addProduct(body)
                    else shopApi.updateProduct(editingId!!, body)
                }

                if (editingId == null) {
                    clearForm() // limpia y muestra snackbar con acción
                } else {
                    Toast.makeText(requireContext(), "Producto '${result.name}' guardado", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }

            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                Toast.makeText(requireContext(), raw ?: e.message(), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Error al guardar", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
                btnSave.isEnabled = true
            }
        }
    }private fun clearForm() {
        // Limpiar campos
        etName.text?.clear()
        etDescription.text?.clear()
        etPrice.text?.clear()
        etStock.text?.clear()
        etBrand.text?.clear()
        etCategory.text?.clear()

        // Limpiar imagen
        ivPreview.setImageDrawable(null)
        // Restablecemos la selección de imágenes para próximas creaciones
        selectedUris.clear()
        existingImages = emptyList()

        // Modo crear
        editingId = null
        btnSave.text = "Guardar"

        // Ocultar teclado
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)

        // Subir al inicio del form (si tu layout tiene un ScrollView con id `scroll`)
        view?.findViewById<NestedScrollView>(R.id.scroll)?.smoothScrollTo(0, 0)
        etName.requestFocus()

        // Snackbar con acción: ir a la lista de productos
        Snackbar.make(requireView(), "Producto guardado", Snackbar.LENGTH_LONG)
            .setAction("Ver lista") {
                val bottom = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
                // ⚠️ Reemplaza 'R.id.products' por el ID real del ítem "Productos" en tu menú inferior.
                // Suele ser uno de estos: R.id.products, R.id.nav_products, R.id.menu_products
                bottom.selectedItemId =R.id.nav_products
            }
            .show()
    }
}

