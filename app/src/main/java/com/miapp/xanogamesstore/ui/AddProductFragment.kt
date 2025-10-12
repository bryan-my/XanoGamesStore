package com.miapp.xanogamesstore.ui

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
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

class AddProductFragment : Fragment() {

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

    private var selectedImageUri: Uri? = null

    private val picker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            ivPreview.setImageURI(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
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
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPick.setOnClickListener {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSave.setOnClickListener { createProduct() }
    }

    private fun createProduct() {
        val name = etName.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val price = etPrice.text.toString().toDoubleOrNull()
        val stock = etStock.text.toString().toIntOrNull()
        val brand = etBrand.text.toString().trim()
        val category = etCategory.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || price == null || stock == null) {
            Toast.makeText(requireContext(), "Completa los campos obligatorios", Toast.LENGTH_SHORT)
                .show()
            return
        }

        btnSave.isEnabled = false
        progress.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1) Subir imagen si hay  ⬅️  CAMBIAR: usar UploadResponse en lugar de ImageInput
                var images: List<UploadResponse> = emptyList()
                selectedImageUri?.let { uri ->
                    val (part, _) = uriToContentPart(requireContext().contentResolver, uri)
                    val upApi = ApiClient.upload(requireContext()).create(UploadService::class.java)
                    val up = withContext(Dispatchers.IO) { upApi.upload(part) }
                    // Enviar el OBJETO COMPLETO que devuelve /upload
                    images = listOf(up)
                }

                // 2) Crear producto (no cambia, solo que 'image' ahora es List<UploadResponse>)
                val shopApi = ApiClient.shop(requireContext()).create(ProductService::class.java)
                val body = CreateProductBody(
                    name = name,
                    description = description,
                    price = price,
                    stock = stock,
                    brand = brand,
                    category = category,
                    image = images
                )
                withContext(Dispatchers.IO) { shopApi.addProduct(body) }

                // 3) limpiar UI (igual)
                etName.setText(""); etDescription.setText("")
                etPrice.setText(""); etStock.setText("")
                etBrand.setText(""); etCategory.setText("")
                ivPreview.setImageDrawable(null); selectedImageUri = null
                Toast.makeText(requireContext(), "Producto creado", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), extractHttpError(e), Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
                btnSave.isEnabled = true
            }
        }
    }
}
