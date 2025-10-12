package com.miapp.xanogamesstore.api

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun uriToMultipart(
    resolver: ContentResolver,
    uri: Uri,
    partName: String = "file",
    fileName: String = "upload_${System.currentTimeMillis()}"
): MultipartBody.Part {
    val input = resolver.openInputStream(uri)!!
    val tmp = File.createTempFile("xano_", fileName)
    FileOutputStream(tmp).use { out -> input.copyTo(out) }
    val media = resolver.getType(uri) ?: "application/octet-stream"
    val body = tmp.asRequestBody(media.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, tmp.name, body)
}