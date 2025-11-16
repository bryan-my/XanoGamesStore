package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.model.Product

class OrderDetailAdapter(private val items: MutableList<Product>)
    : RecyclerView.Adapter<OrderDetailAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val iv = v.findViewById<ImageView>(R.id.ivProduct)
        private val tvName = v.findViewById<TextView>(R.id.tvProductName)
        private val tvPrice = v.findViewById<TextView>(R.id.tvProductPrice)
        fun bind(p: Product) {
            tvName.text = p.name
            tvPrice.text = "$ ${"%.2f".format(p.price)}"
            val url = ApiClient.fileUrl(p.image?.firstOrNull()?.path)
            Glide.with(iv.context)
                .load(url)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(iv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    fun replaceAll(newItems: List<Product>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
