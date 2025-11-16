// app/src/main/java/com/miapp/xanogamesstore/api/Services.kt
package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.Product
import retrofit2.http.*
import com.miapp.xanogamesstore.model.CartDto
import com.miapp.xanogamesstore.model.CreateCartBody
import com.miapp.xanogamesstore.model.UpdateProductStockBody

typealias ImagePayload = UploadResponse

data class CreateProductBody(
    val name: String,
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

/**
 * Servicio para crear carritos, actualizar stock y obtener los carritos.
 * Debe contener todos los métodos, incluyendo `getCarts`.
 */
interface CartService {

    @POST("cart")
    suspend fun createCart(@Body body: CreateCartBody): CartDto

    /**
     * Actualiza el stock de un producto.
     */
    @PATCH("product/{product_id}")
    suspend fun updateProductStock(
        @Path("product_id") productId: Int,
        @Body body: UpdateProductStockBody
    ): Product

    /**
     * Devuelve la lista de carritos/pedidos existentes. Si pasas un `user_id`,
     * Xano filtrará los registros; si no, devolverá todos.
     */
    @GET("cart")
    suspend fun getCarts(@Query("user_id") userId: Int? = null): List<CartDto>

    @GET("cart/{id}")
    suspend fun getCart(@Path("id") id: Int): CartDto

    @PATCH("cart/{id}")
    suspend fun updateCart(
        @Path("id") id: Int,
        @Body body: com.miapp.xanogamesstore.model.UpdateCartBody
    ): CartDto
}

