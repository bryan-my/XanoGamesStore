package com.miapp.xanogamesstore.ui

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
import okhttp3.MultipartBody

class AddProductFragment : Fragment() {

    private var selectedImageUri: Uri? = null

    private lateinit var etName: EditText
    private lateinit var etDescription: EditText
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText
    private lateinit var etBrand: EditText
    private lateinit var etCategory: EditText
    private lateinit var ivPreview: ImageView
    private lateinit var btnPick: Button
    private lateinit var btnSave: Button
    private lateinit var progress: ProgressBar

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            selectedImageUri = uri
            ivPreview.setImageURI(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSave.setOnClickListener { createProduct() }
    }

    private fun createProduct() {
        val name = etName.text.toString().trim()
        val desc = etDescription.text.toString().trim()
        val price = etPrice.text.toString().trim().toDoubleOrNull()
        val stock = etStock.text.toString().trim().toIntOrNull()
        val brand = etBrand.text.toString().trim()
        val category = etCategory.text.toString().trim()

        if (name.isEmpty()) { etName.error = "Requerido"; return }
        if (desc.isEmpty()) { etDescription.error = "Requerido"; return }
        if (price == null) { etPrice.error = "Número inválido"; return }
        if (stock == null) { etStock.error = "Número inválido"; return }

        progress.visibility = View.VISIBLE
        btnSave.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1) Subir imagen (opcional)
                val uploadApi = ApiClient.shop(requireContext()).create(UploadService::class.java)
                val part: MultipartBody.Part? = selectedImageUri?.let {
                    uriToMultipart(requireContext().contentResolver, it, "file")
                }
                val uploaded: UploadResponse? = if (part != null) {
                    withContext(Dispatchers.IO) { uploadApi.upload(part) }
                } else null

                // 2) Crear producto (POST /product)
                val productApi = ApiClient.shop(requireContext()).create(ProductService::class.java)
                val body = CreateProductBody(
                    name = name,
                    description = desc,
                    price = price,
                    stock = stock,
                    brand = brand,
                    category = category,
                    image = uploaded?.file?.path?.let { listOf(mapOf("path" to it)) } ?: emptyList()
                )
                withContext(Dispatchers.IO) { productApi.addProduct(body) }

                // limpiar UI
                etName.setText(""); etDescription.setText(""); etPrice.setText("")
                etStock.setText(""); etBrand.setText(""); etCategory.setText("")
                ivPreview.setImageDrawable(null); selectedImageUri = null
                Toast.makeText(requireContext(), "Producto creado", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
                btnSave.isEnabled = true
            }
        }
    }
}