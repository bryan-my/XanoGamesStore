package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.ProductService
import com.miapp.xanogamesstore.model.CreateProductRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddProductFragment : Fragment() {

    private lateinit var service: ProductService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        val retrofit = ApiClient.createRetrofit { SessionPrefs.getToken(requireContext()) }
        service = retrofit.create(ProductService::class.java)

        val etName = v.findViewById<EditText>(R.id.etName)
        val etDesc = v.findViewById<EditText>(R.id.etDesc)
        val etPrice = v.findViewById<EditText>(R.id.etPrice)
        val etStock = v.findViewById<EditText>(R.id.etStock)
        val etBrand = v.findViewById<EditText>(R.id.etBrand)
        val etCategory = v.findViewById<EditText>(R.id.etCategory)
        val btn = v.findViewById<Button>(R.id.btnCreate)

        btn.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val price = etPrice.text.toString().toDoubleOrNull()
            val stock = etStock.text.toString().toIntOrNull()
            val brand = etBrand.text.toString().trim().ifBlank { null }
            val category = etCategory.text.toString().trim().ifBlank { null }

            if (name.isEmpty() || desc.isEmpty() || price == null || stock == null) {
                Toast.makeText(requireContext(), "Completa nombre, descripción, precio y stock", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val created = withContext(Dispatchers.IO) {
                        service.createProduct(
                            CreateProductRequest(
                                name = name,
                                description = desc,
                                price = price,
                                stock = stock,
                                brand = brand,
                                category = category,
                                images = emptyList()
                            )
                        )
                    }
                    Toast.makeText(requireContext(), "Creado: ${created.name}", Toast.LENGTH_LONG).show()
                    etName.text.clear(); etDesc.text.clear(); etPrice.text.clear()
                    etStock.text.clear(); etBrand.text.clear(); etCategory.text.clear()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
