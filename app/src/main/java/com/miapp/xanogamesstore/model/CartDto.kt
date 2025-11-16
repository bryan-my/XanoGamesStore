package com.miapp.xanogamesstore.model

data class CreateCartBody(
    val user_id: Int,
    val total: Int,
    val product_id: List<Int>
)

data class CartDto(
    val id: Int,
    val user_id: Int,
    val total: Int,
    val product_id: List<Int>,
    val approved: Boolean
)