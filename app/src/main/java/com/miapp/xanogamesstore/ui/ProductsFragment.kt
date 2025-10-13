package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.ProductService
import com.miapp.xanogamesstore.model.CartManager
import com.miapp.xanogamesstore.model.Product
import com.miapp.xanogamesstore.ui.adapter.ProductAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductsFragment : Fragment() {

    private lateinit var list: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: ProductAdapter
    private val items = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        s: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_products, container, false)
        list = v.findViewById(R.id.recycler)
        progress = v.findViewById(R.id.progress)

        // Adapter con callback para "Agregar al carrito"
        adapter = ProductAdapter(items) { product ->
            CartManager.add(product, 1)
            Toast.makeText(requireContext(), "Añadido: ${product.name}", Toast.LENGTH_SHORT).show()
        }

        list.layoutManager = LinearLayoutManager(requireContext())
        list.adapter = adapter
        return v
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        progress.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = ApiClient.shop(requireContext()).create(ProductService::class.java)
                val data = withContext(Dispatchers.IO) { api.getProducts() }
                adapter.replaceAll(data)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    e.message ?: "Error cargando productos",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
