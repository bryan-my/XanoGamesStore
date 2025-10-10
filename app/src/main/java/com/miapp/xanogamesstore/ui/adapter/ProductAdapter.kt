package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.model.Product

class ProductAdapter(
    private var items: List<Product> = emptyList()
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    fun submit(newItems: List<Product>) {
        items = newItems
        notifyDataSetChanged()
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvName = v.findViewById<TextView>(R.id.tvName)
        private val tvBrandCat = v.findViewById<TextView>(R.id.tvBrandCategory)
        private val tvPriceStock = v.findViewById<TextView>(R.id.tvPriceStock)

        fun bind(p: Product) {
            tvName.text = p.name
            val bc = listOfNotNull(p.brand, p.category).joinToString(" • ")
            tvBrandCat.text = if (bc.isBlank()) "—" else bc
            tvPriceStock.text = "USD ${"%.2f".format(p.price)} • Stock: ${p.stock}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size
}