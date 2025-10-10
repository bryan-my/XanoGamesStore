package com.miapp.xanogamesstore.model

/* Adapta los nombres a los campos de tu tabla Xano si difieren */
data class Product(
    val id: Int? = null,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val brand: String? = null,
    val category: String? = null,
    val images: List<String>? = null
)

data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val brand: String? = null,
    val category: String? = null,
    val images: List<String> = emptyList()
)