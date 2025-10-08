package com.miapp.xanogamesstore.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val brand: String? = null,
    val category: String? = null,
    val images: List<String>? = emptyList()
)

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val brand: String? = null,
    val category: String? = null,
    val images: List<String>? = emptyList()
)