package com.miapp.xanogamesstore.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.model.XanoImage

class ThumbnailAdapter(
    private val images: List<XanoImage>,
    private val onThumbnailClick: (Int) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.ivThumbnail)

        fun bind(image: XanoImage, position: Int) {
            val url = ApiClient.fileUrl(image.path)
            Glide.with(imageView.context).load(url).into(imageView)
            itemView.setOnClickListener { onThumbnailClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(images[position], position)
    }

    override fun getItemCount(): Int = images.size
}
