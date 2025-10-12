package com.miapp.xanogamesstore.model

data class LoginRequest(
    val email: String,
    val password: String
)

// Ajusta "authToken" vs "token" según Xano
data class AuthResponse(
    val authToken: String,
    val user: User      // <-- usa la clase User definida en UserModels.kt
)