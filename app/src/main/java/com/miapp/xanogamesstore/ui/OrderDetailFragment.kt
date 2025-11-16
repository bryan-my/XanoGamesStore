// app/src/main/java/com/miapp/xanogamesstore/ui/OrderDetailFragment.kt
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
import com.miapp.xanogamesstore.api.ProductService
import com.miapp.xanogamesstore.api.extractHttpError
import com.miapp.xanogamesstore.model.CartDto
import com.miapp.xanogamesstore.model.Product
import com.miapp.xanogamesstore.ui.adapter.OrderDetailAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class OrderDetailFragment : Fragment() {

    private var orderId: Int = 0
    private lateinit var rvDetail: RecyclerView
    private lateinit var tvTitle: TextView
    private val adapter by lazy { OrderDetailAdapter(mutableListOf()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments?.getInt(ARG_ORDER_ID) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_order_detail, container, false)
        rvDetail = view.findViewById(R.id.rvOrderDetail)
        tvTitle = view.findViewById(R.id.tvOrderDetailTitle)

        rvDetail.layoutManager = LinearLayoutManager(requireContext())
        rvDetail.adapter = adapter

        // Título dinámico con el número de pedido
        tvTitle.text = getString(R.string.title_order_detail, orderId)

        loadDetails()
        return view
    }

    private fun loadDetails() {
        val ctx = requireContext()
        viewLifecycleOwner.lifecycleScope.launch {
            val cartService = ApiClient.shop(ctx).create(CartService::class.java)
            val productService = ApiClient.shop(ctx).create(ProductService::class.java)
            try {
                val cart: CartDto = withContext(Dispatchers.IO) { cartService.getCart(orderId) }
                val productIds: List<Int> = when (val ids = cart.product_id) {
                    is List<*> -> ids.mapNotNull {
                        when (it) {
                            is Double -> it.toInt()
                            is Int -> it
                            else -> null
                        }
                    }
                    else -> emptyList()
                }
                val products = mutableListOf<Product>()
                for (pid in productIds) {
                    try {
                        val p = withContext(Dispatchers.IO) { productService.getProduct(pid) }
                        products.add(p)
                    } catch (e: Exception) {
                        // si falla la consulta de un producto, lo ignoramos
                        e.printStackTrace()
                    }
                    // añadimos una pausa de 2 segundos entre cada petición para no exceder el rate limit
                    delay(2000L)
                }
                adapter.replaceAll(products)
            } catch (e: Exception) {
                val msg = extractHttpError(e)
                val friendlyMsg = if (e is HttpException && e.code() == 429) {
                    "Demasiadas peticiones, intenta de nuevo más tarde"
                } else {
                    msg
                }
                Toast.makeText(ctx, "Error al cargar el detalle: $friendlyMsg", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"
        fun newInstance(orderId: Int) = OrderDetailFragment().apply {
            arguments = Bundle().apply { putInt(ARG_ORDER_ID, orderId) }
        }
    }
}
