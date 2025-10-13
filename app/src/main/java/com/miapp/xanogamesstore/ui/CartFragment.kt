package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.CartManager
import com.miapp.xanogamesstore.ui.adapter.CartItemAdapter
import java.util.Locale

class CartFragment : Fragment() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnClear: Button
    private lateinit var btnCheckout: Button

    private val adapter by lazy {
        CartItemAdapter(
            items = mutableListOf(),
            onPlus = { ci ->
                CartManager.add(ci.product, 1)
                refresh()
            },
            onMinus = { ci ->
                CartManager.decrease(ci.product)   // ver función abajo
                refresh()
            },
            onRemove = { ci ->
                CartManager.remove(ci.product)     // ver función abajo
                refresh()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_cart, container, false)

        tvEmpty = v.findViewById(R.id.tvEmpty)
        rvCart = v.findViewById(R.id.rvCart)
        tvTotal = v.findViewById(R.id.tvTotal)
        btnClear = v.findViewById(R.id.btnClear)
        btnCheckout = v.findViewById(R.id.btnCheckout)

        rvCart.layoutManager = LinearLayoutManager(requireContext())
        rvCart.adapter = adapter

        btnClear.setOnClickListener {
            if (CartManager.items().isEmpty()) {
                Toast.makeText(requireContext(), "El carrito ya está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CartManager.clear()
            refresh()
        }

        btnCheckout.setOnClickListener {
            val total = CartManager.total()
            if (total <= 0.0) {
                Toast.makeText(requireContext(), "No hay productos para pagar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), "Pagado con éxito", Toast.LENGTH_LONG).show()
            CartManager.clear()
            refresh()
        }

        refresh()
        return v
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        // Carga datos del carrito en el adapter
        adapter.replaceAll(CartManager.items())
        updateTotal()
        updateEmptyState()
    }

    private fun updateTotal() {
        val total = CartManager.total()
        tvTotal.text = String.format(Locale.US, "$ %.2f", total)
    }

    private fun updateEmptyState() {
        val empty = CartManager.items().isEmpty()
        tvEmpty.visibility = if (empty) View.VISIBLE else View.GONE
        rvCart.visibility = if (empty) View.GONE else View.VISIBLE
        btnCheckout.isEnabled = !empty
        btnClear.isEnabled = !empty
    }
}