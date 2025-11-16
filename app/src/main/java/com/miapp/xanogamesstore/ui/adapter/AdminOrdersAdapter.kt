package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        private val tvUser: TextView = v.findViewById(R.id.tvOrderUser)
        private val tvStatus: TextView = v.findViewById(R.id.tvOrderStatus)
        private val btnApprove: Button = v.findViewById(R.id.btnOrderApprove)

        fun bind(order: CartDto) {
            val nf = NumberFormat.getCurrencyInstance(Locale.getDefault())
            val totalFormatted = nf.format(order.total.toDouble())
            tvInfo.text = "Pedido #${order.id} - $totalFormatted"
            tvUser.text = "Usuario #${order.user_id}"
            tvStatus.text = if (order.approved)
                itemView.context.getString(R.string.orders_approved)
            else
                itemView.context.getString(R.string.orders_pending)

            if (order.approved) {
                btnApprove.text = itemView.context.getString(R.string.orders_approved)
                btnApprove.isEnabled = false
            } else {
                btnApprove.text = itemView.context.getString(R.string.action_approve)
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
