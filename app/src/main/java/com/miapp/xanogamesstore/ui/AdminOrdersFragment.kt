package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.*
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
import com.miapp.xanogamesstore.model.UpdateCartBody
import com.miapp.xanogamesstore.ui.adapter.AdminOrdersAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AdminOrdersFragment : Fragment() {

    private lateinit var rvPending: RecyclerView
    private lateinit var rvApproved: RecyclerView
    private val pendingAdapter by lazy {
        AdminOrdersAdapter(mutableListOf()) { cart -> approveOrder(cart) }
    }
    private val approvedAdapter by lazy {
        // Para aprobados no hay acción de aprobar
        AdminOrdersAdapter(mutableListOf()) { }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_admin_orders, container, false)
        rvPending = view.findViewById(R.id.rvPendingOrders)
        rvApproved = view.findViewById(R.id.rvApprovedOrders)
        rvPending.layoutManager = LinearLayoutManager(requireContext())
        rvApproved.layoutManager = LinearLayoutManager(requireContext())
        rvPending.adapter = pendingAdapter
        rvApproved.adapter = approvedAdapter
        return view
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    /**
     * Obtiene todos los pedidos y separa en pendientes/aprobados.
     */
    private fun loadOrders() {
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.shop(ctx).create(CartService::class.java)
            try {
                val carts: List<CartDto> = withContext(Dispatchers.IO) {
                    api.getCarts(null)
                }
                val pending = carts.filter { !it.approved }
                val approved = carts.filter { it.approved }
                pendingAdapter.replaceAll(pending)
                approvedAdapter.replaceAll(approved)
            } catch (e: Exception) {
                val msg = extractHttpError(e)
                val friendly = if (e is HttpException && e.code() == 429)
                    "Demasiadas peticiones, intenta de nuevo más tarde"
                else msg
                Toast.makeText(ctx, "Error al cargar pedidos: $friendly", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Llama al backend para marcar un pedido como aprobado
     * y recarga las listas al terminar.
     */
    private fun approveOrder(cart: CartDto) {
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val api = ApiClient.shop(ctx).create(CartService::class.java)
            try {
                withContext(Dispatchers.IO) {
                    api.updateCart(cart.id, UpdateCartBody(true))
                }
                Toast.makeText(ctx, "Pedido #${cart.id} aprobado", Toast.LENGTH_SHORT).show()
                // Esperar 2 segundos para no saturar el backend
                delay(2000L)
                loadOrders()
            } catch (e: Exception) {
                val msg = extractHttpError(e)
                val friendly = if (e is HttpException && e.code() == 429)
                    "Demasiadas peticiones, intenta de nuevo más tarde"
                else msg
                Toast.makeText(ctx, "Error al aprobar pedido: $friendly", Toast.LENGTH_LONG).show()
            }
        }
    }
}
