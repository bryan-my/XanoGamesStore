package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.model.Product

class ProductAdapter(
    private val items: MutableList<Product>,
    private val showAddButton: Boolean,
    private val onItemClick: (Product) -> Unit, // Nuevo callback para el clic en el ítem
    private val onAddToCart: (Product) -> Unit,
    private val onEdit: ((Product) -> Unit)? = null,
    private val onDelete: ((Product) -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val iv: ImageView = v.findViewById(R.id.iv)
        private val tvName: TextView = v.findViewById(R.id.tvName)
        private val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        private val btnAdd: Button = v.findViewById(R.id.btnAddToCart)
        private val adminActions: LinearLayout = v.findViewById(R.id.adminActions)
        private val btnEdit: Button = v.findViewById(R.id.btnEdit)
        private val btnDelete: Button = v.findViewById(R.id.btnDelete)

        fun bind(p: Product) {
            tvName.text = p.name
            tvPrice.text = "$ ${"%.2f".format(p.price)}"

            val url = ApiClient.fileUrl(p.image?.firstOrNull()?.path)
            Glide.with(iv.context)
                .load(url)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(iv)

            // Configurar el clic en el ítem completo
            itemView.setOnClickListener { onItemClick(p) }

            // Cliente
            btnAdd.visibility = if (showAddButton) View.VISIBLE else View.GONE
            btnAdd.setOnClickListener { onAddToCart(p) }

            // Admin
            adminActions.visibility = if (showAddButton) View.GONE else View.VISIBLE
            btnEdit.setOnClickListener { onEdit?.invoke(p) }
            btnDelete.setOnClickListener { onDelete?.invoke(p) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
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
