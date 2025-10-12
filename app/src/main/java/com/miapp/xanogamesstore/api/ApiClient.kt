package com.miapp.xanogamesstore.api

import android.content.Context
import com.miapp.xanogamesstore.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private fun baseClient(context: Context): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

    // Grupo Authentication (login)
    fun auth(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_BASE_AUTH) // /api:300741/
            .client(baseClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Grupo E-commerce Backend API (#300742)
    fun shop(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_BASE_SHOP) // /api:300742/
            .client(baseClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Convierte paths de Xano (ej. "/file/abc...") a URL absoluta
    fun fileUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path else BuildConfig.XANO_ORIGIN+ path
    }
}