package com.miapp.xanogamesstore.api

import retrofit2.HttpException
import java.io.IOException

fun extractHttpError(e: Throwable): String {
    return when (e) {
        is HttpException -> {
            val code = e.code()
            val msg = try { e.response()?.errorBody()?.string() } catch (_: IOException) { null }
            "HTTP $code ${msg ?: e.message()}"
        }
        else -> e.message ?: e.toString()
    }
}
