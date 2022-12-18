package com.foodapp.app.api

class RestResponse<T> {

    private var data: T? = null

    private var message: String? = null

    private var status: String? = null

    private var mobile: String? = null
    private var email: String? = null

    fun getData(): T? {
        return data
    }

    fun getMessage(): String? {
        return message
    }

    fun getStatus(): String? {
        return status
    }

    fun getMobile(): String? {
        return mobile
    }

    fun getEmail(): String? {
        return email
    }


    private var currency: String? = null

    fun getCurrency(): String? {
        return currency
    }

    private var min_order_amount: String? = null

    fun getMin_order_amount(): String? {
        return min_order_amount
    }

    private var max_order_amount: String? = null

    fun getMax_order_amount(): String? {
        return max_order_amount
    }

    private var max_order_qty: String? = null

    fun getMax_order_qty(): String? {
        return max_order_qty
    }

    private var referral_amount: String? = null

    fun getReferral_amount(): String? {
        return referral_amount
    }

    private var map: String? = null

    fun getMap(): String? {
        return map
    }
}