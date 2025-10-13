package com.miapp.xanogamesstore.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Carrito en memoria (singleton). No usa red ni DB.
 */
object CartManager {

    private val items = mutableListOf<CartItem>()
    private val bag = mutableListOf<CartItem>()

    private val _live = MutableLiveData<List<CartItem>>(emptyList())
    val live: LiveData<List<CartItem>> = _live

    fun items(): MutableList<CartItem> = items


    fun add(p: Product, qty: Int = 1) {
        val existing = items.firstOrNull { it.product.id == p.id }
        if (existing != null) existing.quantity += qty else items.add(CartItem(p, qty))
    }
    fun clear() = items.clear()

    fun total(): Double = items.sumOf { (it.product.price ?: 0.0) * it.quantity }
    fun changeQty(productId: Int?, qty: Int) {
        if (productId == null) return
        val i = bag.indexOfFirst { it.product.id == productId }
        if (i >= 0) {
            bag[i].quantity = qty.coerceAtLeast(1)
            notifyChange()
        }
    }

    fun remove(productId: Int?) {
        if (productId == null) return
        bag.removeAll { it.product.id == productId }
        notifyChange()
    }



    fun count(): Int = bag.sumOf { it.quantity }

    fun decrease(p: Product) {
        val it = items.firstOrNull { it.product.id == p.id } ?: return
        if (it.quantity > 1) it.quantity-- else items.remove(it)
    }

    fun remove(p: Product) {
        items.removeAll { it.product.id == p.id }
    }


    private fun notifyChange() { _live.value = bag.toList() }
}