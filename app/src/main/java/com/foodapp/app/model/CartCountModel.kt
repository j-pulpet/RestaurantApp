package com.foodapp.app.model

class CartCountModel {
    private var message: String? = null

    private var cart: String? = null

    private var status: String? = null

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getCart(): String? {
        return cart
    }

    fun setCart(cart: String?) {
        this.cart = cart
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }
}