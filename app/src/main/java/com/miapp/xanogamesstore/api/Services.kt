package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.Product
import retrofit2.http.*

data class CreateProductBody(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val brand: String,
    val category: String,
    // Xano product.image es [image], así que enviamos una lista
    val image: List<Map<String, String>> = emptyList()
)

interface ProductService {
    @GET("product")
    suspend fun getProducts(): List<Product>

    @POST("product")
    suspend fun addProduct(@Body body: CreateProductBody): Product

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)
}