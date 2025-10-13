package com.miapp.xanogamesstore.model

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val subtotal: Double get() = (product.price ?: 0.0) * quantity
}