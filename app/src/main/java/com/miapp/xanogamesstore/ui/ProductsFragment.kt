package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
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
    private lateinit var shimmerContainer: ShimmerFrameLayout
    private lateinit var search: SearchView

    private val allItems = mutableListOf<Product>()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_products, container, false)

        val session = SessionPrefs(requireContext())
        val isAdmin = session.userRole == "admin"

        list = v.findViewById(R.id.recycler)
        shimmerContainer = v.findViewById(R.id.shimmer_view_container)
        search = v.findViewById(R.id.search)

        adapter = ProductAdapter(
            items = mutableListOf(),
            showAddButton = !isAdmin,
            onItemClick = { product ->
                parentFragmentManager.commit {
                    replace(R.id.homeContainer, ProductDetailFragment.newInstance(product.id))
                    addToBackStack(null)
                }
            },
            onAddToCart = { product ->
                try {
                    CartManager.add(product)
                    Toast.makeText(requireContext(), "Agregado al carrito", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "No se pudo agregar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            onEdit = { product ->
                parentFragmentManager.commit {
                    replace(R.id.homeContainer, AddProductFragment.edit(product.id))
                    addToBackStack(null)
                }
            },
            onDelete = { product ->
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val api = ApiClient.shop(requireContext()).create(ProductService::class.java)
                        withContext(Dispatchers.IO) { api.deleteProduct(product.id) }
                        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                        load() // recarga
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message ?: "Error al eliminar", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )

        list.layoutManager = GridLayoutManager(requireContext(), 2)
        list.adapter = adapter

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { applyFilter(query); return true }
            override fun onQueryTextChange(newText: String?): Boolean { applyFilter(newText); return true }
        })

        return v
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        list.visibility = View.GONE
        shimmerContainer.visibility = View.VISIBLE
        shimmerContainer.startShimmer()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = ApiClient.shop(requireContext()).create(ProductService::class.java)
                val data = withContext(Dispatchers.IO) { api.getProducts() }
                allItems.clear()
                allItems.addAll(data)
                adapter.replaceAll(allItems)
                applyFilter(search.query?.toString())
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Error cargando productos", Toast.LENGTH_LONG).show()
            } finally {
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE
                list.visibility = View.VISIBLE
            }
        }
    }

    private fun applyFilter(q: String?) {
        val query = (q ?: "").trim().lowercase()
        if (query.isEmpty()) {
            adapter.replaceAll(allItems)
            return
        }
        val filtered = allItems.filter { p ->
            p.name.lowercase().contains(query) ||
                    p.category.lowercase().contains(query) ||
                    p.description.lowercase().contains(query) ||
                    p.brand.lowercase().contains(query)
        }
        adapter.replaceAll(filtered)
    }
}
