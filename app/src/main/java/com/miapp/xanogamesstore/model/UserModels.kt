package com.miapp.xanogamesstore.model

data class User(
    val id: Int,
    val name: String? = null,
    val email: String,
    val role: String? = "cliente"
)