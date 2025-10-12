package com.miapp.xanogamesstore.api

import android.content.Context
import com.miapp.xanogamesstore.ui.SessionPrefs
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val session = SessionPrefs(context.applicationContext)

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val token = session.authToken
        val newReq = if (!token.isNullOrBlank()) {
            req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else req
        return chain.proceed(newReq)
    }
}