package com.miapp.xanogamesstore.api

import com.miapp.xanogamesstore.model.*
import retrofit2.http.*
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part






data class UploadResponse(val url: String) // adapta al JSON real de Xano

interface UploadService {
    @Multipart
    @POST("upload") // <-- ajusta a la ruta real en tu Xano si difiere
    suspend fun uploadImage(@Part file: MultipartBody.Part): UploadResponse
}
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
