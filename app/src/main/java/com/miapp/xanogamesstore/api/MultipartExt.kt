package com.miapp.xanogamesstore.api

import android.content.ContentResolver
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File

fun uriToMultipart(
    resolver: ContentResolver,
    uri: Uri,
    partName: String = "file",
    fileName: String = "image.jpg"
): MultipartBody.Part {
    val type = resolver.getType(uri) ?: "image/jpeg"

    val requestBody = object : RequestBody() {
        override fun contentType() = type.toMediaTypeOrNull()
        override fun writeTo(sink: BufferedSink) {
            resolver.openInputStream(uri)?.use { input ->
                sink.writeAll(input.source())
            }
        }
    }
    return MultipartBody.Part.createFormData(partName, fileName, requestBody)
}
