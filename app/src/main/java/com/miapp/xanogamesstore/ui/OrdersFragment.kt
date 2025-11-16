package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.CartService
import com.miapp.xanogamesstore.api.extractHttpError
import com.miapp.xanogamesstore.model.CartDto
import com.miapp.xanogamesstore.ui.adapter.OrdersAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.miapp.xanogamesstore.ui.OrderDetailFragment

/**
 * Fragmento para mostrar la lista de pedidos realizados por el cliente.
 * Se obtiene la lista desde el backend (Xano) a través de CartService.
 */
class OrdersFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var tvEmpty: TextView

    private lateinit var swipeRefresh: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private val adapter by lazy {
        OrdersAdapter(mutableListOf()) { cart ->
            onOrderClicked(cart)
        }
    }
    private fun onOrderClicked(cart: CartDto) {
        val frag = OrderDetailFragment.newInstance(cart.id)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.homeContainer, frag)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)
        rvOrders = view.findViewById(R.id.rvOrders)
        tvEmpty = view.findViewById(R.id.tvOrdersEmpty)
        swipeRefresh = view.findViewById(R.id.swipeRefreshOrders)
        rvOrders.layoutManager = LinearLayoutManager(requireContext())
        rvOrders.adapter = adapter

        // Configuramos swipe-to-refresh
        swipeRefresh.setOnRefreshListener { loadOrders() }

        loadOrders()
        return view
    }



    /**
     * Consulta los carritos del usuario actual y los muestra. Si no hay usuario,
     * deja la lista vacía. Si ocurre algún error, se muestra un Toast informativo.
     */
    private fun loadOrders() {
        val ctx = requireContext()
        val session = SessionPrefs(ctx)
        val userId = session.userId?.toIntOrNull() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.shop(ctx).create(CartService::class.java)
            try {
                val carts: List<CartDto> = withContext(Dispatchers.IO) {
                    api.getCarts(userId)
                }
                val orders = carts.filter { it.user_id == userId }
                adapter.replaceAll(orders)

                val empty = orders.isEmpty()
                tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
                rvOrders.visibility = if (empty) View.GONE else View.VISIBLE
                swipeRefresh.isRefreshing = false
            } catch (e: Exception) {
                val msg = extractHttpError(e)
                val friendlyMsg =
                    if (e is HttpException && e.code() == 429)
                        "Demasiadas peticiones, intenta de nuevo más tarde"
                    else msg
                Toast.makeText(ctx, "Error al cargar pedidos: $friendlyMsg", Toast.LENGTH_LONG).show()
                swipeRefresh.isRefreshing = false
            }
        }
    }
}
