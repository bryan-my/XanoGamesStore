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

    // === Grupo Authentication ===
    fun auth(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_BASE_AUTH) // .../api:ObzeKtl9/
            .client(baseClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // === Grupo Upload ===
    fun upload(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_BASE_UPLOAD) // .../api:-ukB1aW3/
            .client(baseClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // === Grupo E-commerce Backend API ===
    fun shop(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_BASE_SHOP) // .../api:c_UHqNA3/
            .client(baseClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // Convierte path de Xano a URL absoluta servible
    fun fileUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) path else BuildConfig.XANO_ORIGIN + path
    }
}
