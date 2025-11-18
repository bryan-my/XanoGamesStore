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

/**
 * Fragmento para mostrar la lista de pedidos realizados por el cliente.
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

        swipeRefresh.setOnRefreshListener { loadOrders() }

        loadOrders()
        return view
    }

    private fun loadOrders() {
        val ctx = requireContext()
        val session = SessionPrefs(ctx)
        
        val userIdStr = session.userId
        val userId = userIdStr?.toIntOrNull()

        // Debug temporal: Ver qué ID tenemos
        // Toast.makeText(ctx, "Mi UserID es: $userIdStr", Toast.LENGTH_SHORT).show()

        if (userId == null || userId == 0) {
            adapter.replaceAll(emptyList())
            tvEmpty.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
            swipeRefresh.isRefreshing = false
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.shop(ctx).create(CartService::class.java)
            try {
                val carts: List<CartDto> = withContext(Dispatchers.IO) {
                    api.getCarts(userId)
                }

                // Filtrado estricto
                val myOrders = carts.filter { it.user_id == userId }
                
                // Debug: Si la lista no está vacía pero debería, ver qué IDs llegan
                if (carts.isNotEmpty() && myOrders.isNotEmpty()) {
                   // val firstOrder = myOrders.first()
                   // Toast.makeText(ctx, "Pedido recibido ID: ${firstOrder.id}, UserID: ${firstOrder.user_id}", Toast.LENGTH_LONG).show()
                }

                adapter.replaceAll(myOrders)

                val isEmpty = myOrders.isEmpty()
                tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
                rvOrders.visibility = if (isEmpty) View.GONE else View.VISIBLE
                
            } catch (e: Exception) {
                val msg = extractHttpError(e)
                val friendlyMsg = if (e is HttpException && e.code() == 429)
                        "Demasiadas peticiones, intenta más tarde"
                    else msg
                
                if (isAdded) {
                   Toast.makeText(ctx, "Error: $friendlyMsg", Toast.LENGTH_LONG).show()
                }
            } finally {
                swipeRefresh.isRefreshing = false
            }
        }
    }
}
