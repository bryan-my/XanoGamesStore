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

    private val items = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_products, container, false)

        val session = SessionPrefs(requireContext())
        val isAdmin = session.userRole == "admin"   // “admin” o “user” desde tu backend

        list = v.findViewById(R.id.recycler)
        progress = v.findViewById(R.id.progress)

        adapter = ProductAdapter(
            items = items,
            showAddButton = !isAdmin,                 // si es admin, no mostramos “Agregar”
        ) { product ->
            try {
                CartManager.add(product)             // usa tu CartManager existente
                Toast.makeText(requireContext(), "Agregado al carrito", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "No se pudo agregar: ${e.message}", Toast.LENGTH_LONG).show()
            }
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