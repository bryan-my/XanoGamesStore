package com.miapp.xanogamesstore.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Xano Upload suele devolver al menos { "path": "/uploads/..." }
// dependiendo del bloque puede incluir "url" o "id".
data class UploadResponse(
    val path: String,
    val url: String? = null,
    val id: Long? = null
)

interface UploadService {
    @Multipart
    @POST("upload")
    suspend fun upload(@Part file: MultipartBody.Part): UploadResponse
}