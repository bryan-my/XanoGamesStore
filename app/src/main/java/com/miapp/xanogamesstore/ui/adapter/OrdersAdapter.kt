package com.miapp.xanogamesstore.ui.adapter

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.CartDto
import java.util.Locale

class OrdersAdapter(
    private val items: MutableList<CartDto>,
    private val onItemClick: (CartDto) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvId: TextView = v.findViewById(R.id.tvOrderId)
        private val tvDate: TextView = v.findViewById(R.id.tvOrderDate)
        private val tvTotal: TextView = v.findViewById(R.id.tvOrderTotal)
        private val tvStatus: TextView = v.findViewById(R.id.tvOrderStatus)

        fun bind(order: CartDto) {
            // 1. Rellenar campos de texto
            tvId.text = "Pedido #${order.id}"

            // Formatear el total como moneda
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
            tvTotal.text = currencyFormat.format(order.total.toDouble())

            // 2. Ocultar la fecha ya que no la tenemos en el modelo CartDto
            tvDate.visibility = View.GONE

            // 3. Configurar el estado (texto y fondo)
            val isApproved = order.approved
            tvStatus.text = if (isApproved) "Aprobado" else "Pendiente"

            val backgroundRes = if (isApproved) R.drawable.bg_status_approved else R.drawable.bg_status_pending
            tvStatus.background = ContextCompat.getDrawable(itemView.context, backgroundRes)

            // 4. Configurar el click listener para toda la tarjeta
            itemView.setOnClickListener { onItemClick(order) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun replaceAll(newItems: List<CartDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
