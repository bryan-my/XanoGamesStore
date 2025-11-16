package com.miapp.xanogamesstore.ui.adapter

import android.graphics.Color
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.CartDto
import java.util.Locale

/**
 * Adaptador para listar los pedidos (carritos) realizados por un usuario.
 * Cada fila muestra el identificador del pedido, el total y si está aprobado
 * o pendiente.
 */
class OrdersAdapter(
    private val items: MutableList<CartDto>,
    private val onItemClick: (CartDto) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvInfo: TextView = v.findViewById(R.id.tvOrderInfo)
        private val tvStatus: TextView = v.findViewById(R.id.tvOrderStatus)

        fun bind(order: CartDto) {
            // Formatear el total con separador de miles
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val formattedTotal = currencyFormat.format(order.total.toDouble())
            tvInfo.text = "Pedido #${order.id} - $formattedTotal"

            // Mostrar estado y aplicar color
            val isApproved = order.approved
            tvStatus.text = if (isApproved) "Aprobado" else "Pendiente"
            val color = if (isApproved) Color.parseColor("#4CAF50") else Color.parseColor("#F44336")
            tvStatus.setTextColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    /**
     * Reemplaza el contenido de la lista, ideal al refrescar los pedidos
     */
    fun replaceAll(newItems: List<CartDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}
