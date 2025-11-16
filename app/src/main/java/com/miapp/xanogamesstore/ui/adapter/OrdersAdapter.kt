package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.CartDto

/**
 * Adaptador para listar los pedidos (carritos) realizados por un usuario.
 * Cada fila muestra el identificador del pedido, el total y si está aprobado
 * o pendiente.
 */
class OrdersAdapter(
    private val items: MutableList<CartDto>
) : RecyclerView.Adapter<OrdersAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvInfo: TextView = v.findViewById(R.id.tvOrderInfo)
        private val tvStatus: TextView = v.findViewById(R.id.tvOrderStatus)

        fun bind(order: CartDto) {
            val info = "Pedido #${order.id} - $ ${order.total}"
            tvInfo.text = info
            tvStatus.text = if (order.approved) "Aprobado" else "Pendiente"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

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
