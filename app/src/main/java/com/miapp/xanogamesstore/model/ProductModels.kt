package com.miapp.xanogamesstore.model

data class XanoImage(
    val path: String? = null,   // normalmente viene este
    val name: String? = null,
    val mime: String? = null,
    val size: Long? = null
)

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val brand: String,
    val category: String,
    val image: List<XanoImage>? = emptyList()
)