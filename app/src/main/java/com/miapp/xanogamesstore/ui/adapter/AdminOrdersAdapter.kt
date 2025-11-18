package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.CartDto
import java.text.NumberFormat
import java.util.Locale

class AdminOrdersAdapter(
    private val items: MutableList<CartDto>,
    private val onApprove: (CartDto) -> Unit
) : RecyclerView.Adapter<AdminOrdersAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvInfo: TextView = v.findViewById(R.id.tvOrderInfo)
        private val tvTotal: TextView = v.findViewById(R.id.tvOrderTotal)
        private val tvUser: TextView = v.findViewById(R.id.tvOrderUser)
        private val tvStatus: TextView = v.findViewById(R.id.tvOrderStatus)
        private val btnApprove: Button = v.findViewById(R.id.btnOrderApprove)

        fun bind(order: CartDto) {
            // 1. Rellenar campos de texto
            tvInfo.text = "Pedido #${order.id}"
            tvUser.text = "Usuario #${order.user_id}"

            val nf = NumberFormat.getCurrencyInstance(Locale.getDefault())
            tvTotal.text = nf.format(order.total.toDouble())

            // 2. Configurar el estado (texto y fondo)
            val isApproved = order.approved
            tvStatus.text = if (isApproved) "Aprobado" else "Pendiente"

            val backgroundRes = if (isApproved) R.drawable.bg_status_approved else R.drawable.bg_status_pending
            tvStatus.background = ContextCompat.getDrawable(itemView.context, backgroundRes)

            // 3. Configurar el botón de aprobación
            if (isApproved) {
                btnApprove.text = "Aprobado"
                btnApprove.isEnabled = false
            } else {
                btnApprove.text = "Aprobar"
                btnApprove.isEnabled = true
                btnApprove.setOnClickListener { onApprove(order) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_order, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    fun replaceAll(newItems: List<CartDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
