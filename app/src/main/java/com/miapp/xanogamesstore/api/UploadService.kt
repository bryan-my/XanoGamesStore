package com.miapp.xanogamesstore.api

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class UploadFile(val path: String)
data class UploadResponse(val file: UploadFile)

interface UploadService {
    @Multipart
    @POST("upload")
    suspend fun upload(@Part file: MultipartBody.Part): UploadResponse
}