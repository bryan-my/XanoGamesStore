package com.miapp.xanogamesstore.model

data class XanoImage(
    val path: String? = null,
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
    val image: List<XanoImage>? // <<-- usa este data class
)