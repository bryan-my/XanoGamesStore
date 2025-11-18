package com.miapp.xanogamesstore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.miapp.xanogamesstore.R
import com.miapp.xanogamesstore.api.ApiClient
import com.miapp.xanogamesstore.api.ProductService
import com.miapp.xanogamesstore.model.Product
import com.miapp.xanogamesstore.ui.adapter.ImageSliderAdapter
import com.miapp.xanogamesstore.ui.adapter.ThumbnailAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailFragment : Fragment() {

    private var productId: Int = -1

    private lateinit var viewPager: ViewPager2
    private lateinit var rvThumbnails: RecyclerView
    private lateinit var tvName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getInt(ARG_PRODUCT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)
        viewPager = view.findViewById(R.id.viewPagerImages)
        rvThumbnails = view.findViewById(R.id.rvThumbnails)
        tvName = view.findViewById(R.id.tvProductName)
        tvPrice = view.findViewById(R.id.tvProductPrice)
        tvDescription = view.findViewById(R.id.tvProductDescription)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProductDetails()
    }

    private fun loadProductDetails() {
        if (productId == -1) {
            Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_LONG).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val api = ApiClient.shop(requireContext()).create(ProductService::class.java)
                val product = withContext(Dispatchers.IO) { api.getProduct(productId) }
                displayProduct(product)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayProduct(product: Product) {
        tvName.text = product.name
        tvPrice.text = "$ ${"%.2f".format(product.price)}"
        tvDescription.text = product.description

        product.image?.let { images ->
            viewPager.adapter = ImageSliderAdapter(images)
            rvThumbnails.adapter = ThumbnailAdapter(images) { position ->
                viewPager.setCurrentItem(position, true)
            }
        }
    }

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: Int) = ProductDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PRODUCT_ID, productId)
            }
        }
    }
}
