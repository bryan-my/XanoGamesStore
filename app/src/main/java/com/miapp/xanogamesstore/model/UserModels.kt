package com.miapp.xanogamesstore.model

data class User(
    val id: Int,
    val email: String,
    val name: String?
)
data class UserDto(
    val id: Int,
    val name: String?,   // <- añadir si no estaba
    val email: String
)



