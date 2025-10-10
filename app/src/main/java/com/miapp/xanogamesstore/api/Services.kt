package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.*
import retrofit2.http.*

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("auth/me")
    suspend fun me(): User
}

/* En tu Xano los endpoints son en singular: product */
interface ProductService {
    @GET("product")
    suspend fun getProducts(@Query("q") query: String? = null): List<Product>

    @POST("product")
    suspend fun createProduct(@Body body: CreateProductRequest): Product
}