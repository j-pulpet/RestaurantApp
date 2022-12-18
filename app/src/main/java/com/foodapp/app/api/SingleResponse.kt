package com.foodapp.app.api

class SingleResponse {
    private var message: String? = null

    private var status: String? = null

    fun getMessage(): String? {
        return message
    }

    fun getStatus(): String? {
        return status
    }
}