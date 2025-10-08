package com.miapp.xanogamesstore.model

data class LoginRequest(val email: String, val password: String)

data class AuthResponse(
    val token: String,
    val user: User
)