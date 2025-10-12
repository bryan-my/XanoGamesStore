package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.model.Product
import com.bumptech.glide.Glide




class ProductAdapter(private val items: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val iv: ImageView = v.findViewById(R.id.iv)
        private val tvName: TextView = v.findViewById(R.id.tvName)
        private val tvPrice: TextView = v.findViewById(R.id.tvPrice)

        fun bind(p: Product) {
            tvName.text = p.name
            tvPrice.text = "$${String.format("%.2f", p.price)}"

            val url = ApiClient.fileUrl(p.image?.firstOrNull()?.path)
            if (url != null) Glide.with(iv).load(url).into(iv)
            else iv.setImageResource(R.drawable.ic_image_placeholder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
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