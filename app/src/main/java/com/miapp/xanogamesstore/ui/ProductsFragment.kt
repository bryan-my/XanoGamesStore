package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.ui.adapter.ProductAdapter
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.ProductService
import kotlinx.coroutines.*

class ProductsFragment : Fragment() {

    private lateinit var adapter: ProductAdapter
    private lateinit var progress: ProgressBar
    private lateinit var service: ProductService
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrofit con token si hay sesión
        val retrofit = ApiClient.createRetrofit { SessionPrefs.getToken(requireContext()) }
        service = retrofit.create(ProductService::class.java)

        progress = view.findViewById(R.id.progress)

        val rv = view.findViewById<RecyclerView>(R.id.rvProducts)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductAdapter()
        rv.adapter = adapter

        val sv = view.findViewById<SearchView>(R.id.searchView)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { fetch(query); return true }
            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)      // debounce
                    fetch(newText)
                }
                return true
            }
        })

        fetch(null) // carga inicial
    }

    private fun fetch(query: String?) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                progress.visibility = View.VISIBLE
                val data = withContext(Dispatchers.IO) { service.getProducts(query) }
                adapter.submit(data)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progress.visibility = View.GONE
            }
        }
    }
}
