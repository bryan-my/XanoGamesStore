package com.miapp.xanogamesstore.api

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Convierte un Uri de imagen en el Part que tu endpoint necesita.
 * Campo = "content" (como está configurado en Xano)
 */
fun uriToContentPart(resolver: ContentResolver, uri: Uri): Pair<MultipartBody.Part, String> {
    val mime = resolver.getType(uri) ?: "image/*"

    // obtener nombre de archivo
    var filename = "image_${System.currentTimeMillis()}.jpg"
    resolver.query(uri, null, null, null, null)?.use { c ->
        val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx >= 0 && c.moveToFirst()) {
            c.getString(idx)?.let { filename = it }
        }
    }

    val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
        ?: error("No se pudo abrir la imagen")

    val body = bytes.toRequestBody(mime.toMediaType())

    // ¡OJO AQUÍ! => "content" es el nombre que espera Xano (/upload input)
    val part = MultipartBody.Part.createFormData(
        "content",   // <--- nombre exacto del input en Xano
        filename,
        body
    )
    return part to filename
}
