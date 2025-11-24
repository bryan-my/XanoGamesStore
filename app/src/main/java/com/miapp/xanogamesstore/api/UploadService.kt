package com.miapp.xanogamesstore.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Tu /upload devuelve este objeto (según tu captura)
data class UploadMeta(
    val width: Int? = null,
    val height: Int? = null
)

data class UploadResponse(
    val access: String? = null,
    val path: String,
    val name: String? = null,
    val type: String? = null,
    val size: Long? = null,
    val mime: String? = null,
    val meta: UploadMeta? = null
)

interface UploadService {
    @Multipart
    @POST("upload")
    suspend fun upload(
        @Part content: MultipartBody.Part
    ): UploadResponse
}