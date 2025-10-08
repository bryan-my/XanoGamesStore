package com.miapp.xanogamestore.api

import com.miapp.xanogamesstore.model.*
import retrofit2.http.*

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("auth/me")
    suspend fun me(): User
}

interface ProductService {
    @GET("products")
    suspend fun getProducts(@Query("q") query: String? = null): List<Product>

    @POST("products")
    suspend fun createProduct(@Body body: CreateProductRequest): Product
}