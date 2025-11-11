package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.CartItem

class CartItemAdapter(
    private val items: MutableList<CartItem>,
    private val onPlus: (CartItem) -> Unit,
    private val onMinus: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit,
) : RecyclerView.Adapter<CartItemAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val iv: ImageView = v.findViewById(R.id.ivThumb)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val tvQty: TextView = v.findViewById(R.id.tvQty)
        val tvSubtotal: TextView = v.findViewById(R.id.tvSubtotal)
        val btnPlus: ImageButton = v.findViewById(R.id.btnPlus)
        val btnMinus: ImageButton = v.findViewById(R.id.btnMinus)
        val btnRemove: ImageButton = v.findViewById(R.id.btnRemove)

        fun bind(ci: CartItem) {
            tvName.text = ci.product.name ?: "Producto"
            tvPrice.text = String.format("$ %.2f", (ci.product.price ?: 0.0))
            tvQty.text = ci.quantity.toString()
            tvSubtotal.text = String.format("$ %.2f", ci.subtotal)

            val url = com.miapp.xanogamesstore.api.ApiClient.fileUrl(ci.product.image?.firstOrNull()?.path)
            Glide.with(iv.context)
                .load(url)
                .placeholder(R.drawable.ic_cart)
                .into(iv)

            btnPlus.setOnClickListener { onPlus(ci) }
            btnMinus.setOnClickListener { onMinus(ci) }
            btnRemove.setOnClickListener { onRemove(ci) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    fun replaceAll(newItems: List<CartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
