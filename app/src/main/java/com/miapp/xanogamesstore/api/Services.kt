package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.Product
import retrofit2.http.*

import com.miapp.xanogamesstore.model.CartDto
import com.miapp.xanogamesstore.model.CreateCartBody
import com.miapp.xanogamesstore.model.UpdateProductStockBody
import retrofit2.http.*

// ⚠️ VOLVEMOS a usar el objeto de /upload COMPLETO como payload de imagen:
typealias ImagePayload = UploadResponse

data class CreateProductBody(
    val name: String,                 // ← no uses title
    val description: String,
    val price: Double,
    val stock: Int,
    val brand: String,
    val category: String,
    val image: List<ImagePayload> = emptyList()
)

interface ProductService {
    @GET("product")
    suspend fun getProducts(): List<Product>

    @GET("product/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product

    @POST("product")
    suspend fun addProduct(@Body body: CreateProductBody): Product

    @PATCH("product/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body body: CreateProductBody): Product

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)
}

interface CartService {

    @POST("cart")
    suspend fun createCart(
        @Body body: CreateCartBody
    ): CartDto

    @PATCH("product/{product_id}")
    suspend fun updateProductStock(
        @Path("product_id") productId: Int,
        @Body body: UpdateProductStockBody
    ): Product
}
