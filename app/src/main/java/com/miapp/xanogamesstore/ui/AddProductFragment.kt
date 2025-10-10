package com.miapp.xanogamesstore.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.ProductService
import com.miapp.xanogamesstore.api.UploadService
import com.miapp.xanogamesstore.api.uriToMultipart
import com.miapp.xanogamesstore.model.CreateProductRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddProductFragment : Fragment() {

    private lateinit var productService: ProductService
    private val imagesUrls = mutableListOf<String>() // acumulamos URLs pegadas

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val retrofit = ApiClient.createRetrofit { SessionPrefs.getToken(requireContext()) }
        productService = retrofit.create(ProductService::class.java)

        val etName     = v.findViewById<EditText>(R.id.etName)
        val etDesc     = v.findViewById<EditText>(R.id.etDesc)
        val etPrice    = v.findViewById<EditText>(R.id.etPrice)
        val etStock    = v.findViewById<EditText>(R.id.etStock)
        val etBrand    = v.findViewById<EditText>(R.id.etBrand)
        val etCategory = v.findViewById<EditText>(R.id.etCategory)

        // Para opción A
        val etImageUrl = v.findViewById<EditText>(R.id.etImageUrl)
        val btnAddUrl  = v.findViewById<Button>(R.id.btnAddUrl)

        val btnCreate  = v.findViewById<Button>(R.id.btnCreate)

        // Agregar URL de imagen a la lista
        btnAddUrl.setOnClickListener {
            val url = etImageUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                imagesUrls.add(url)
                Toast.makeText(requireContext(), "URL agregada (${imagesUrls.size})", Toast.LENGTH_SHORT).show()
                etImageUrl.text.clear()
            } else {
                Toast.makeText(requireContext(), "Pega una URL válida", Toast.LENGTH_SHORT).show()
            }
        }

        // Crear producto
        btnCreate.setOnClickListener {
            val name  = etName.text.toString().trim()
            val desc  = etDesc.text.toString().trim()
            val price = etPrice.text.toString().toDoubleOrNull()
            val stock = etStock.text.toString().toIntOrNull()
            val brand = etBrand.text.toString().trim().ifBlank { null }
            val cat   = etCategory.text.toString().trim().ifBlank { null }

            if (name.isEmpty() || desc.isEmpty() || price == null || stock == null) {
                Toast.makeText(requireContext(), "Completa nombre, descripción, precio y stock", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val created = withContext(Dispatchers.IO) {
                        productService.createProduct(
                            CreateProductRequest(
                                name = name,
                                description = desc,
                                price = price,
                                stock = stock,
                                brand = brand,
                                category = cat,
                                images = imagesUrls.toList() // guardamos URLs
                            )
                        )
                    }
                    Toast.makeText(requireContext(), "Creado: ${created.name}", Toast.LENGTH_LONG).show()

                    // limpiar
                    etName.text.clear(); etDesc.text.clear()
                    etPrice.text.clear(); etStock.text.clear()
                    etBrand.text.clear(); etCategory.text.clear()
                    imagesUrls.clear()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}