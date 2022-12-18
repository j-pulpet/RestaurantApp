package com.foodapp.app.model

class CartCountModel {
    private var message: String? = null

    private var cart: String? = null

    private var status: String? = null

    fun getMessage(): String? {
        return message
    }

    fun getCart(): String? {
        return cart
    }

    fun getStatus(): String? {
        return status
    }
}