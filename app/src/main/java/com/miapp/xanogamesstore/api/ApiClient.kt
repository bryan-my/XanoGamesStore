package com.miapp.xanogamesstore.api

import com.miapp.xanogamestore.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    fun createRetrofit(tokenProvider: () -> String?): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.XANO_BASE) // viene de build.gradle.kts
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}