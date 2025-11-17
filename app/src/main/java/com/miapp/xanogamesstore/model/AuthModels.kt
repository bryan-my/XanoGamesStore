package com.miapp.xanogamesstore.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

// Ajusta "authToken" vs "token" según Xano
data class AuthResponse(
    val authToken: String,
    val user: User      // <-- usa la clase User definida en UserModels.kt
)

data class LoginBody(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("authToken") val authToken: String
)

data class UserDto(
    val id: Int,
    val name: String?,
    val email: String,
    val role: String?,
    val active: Boolean? = null
)

data class UserMe(
    val id: Int,
    val email: String,
    val name: String?,
    val role: String?,
    val active: Boolean? = null
)
data class SignupBody(
    val email: String,
    val password: String,
    val name: String? = null
)