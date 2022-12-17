package com.foodapp.app.api

class RestResponse<T> {

    private var data:T?=null

    private var message: String? = null

    private var status: String? = null

    fun getData():T? {
        return data
    }

    fun setData(data: T?) {
        this.data = data
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    private var currency: String? = null

    fun getCurrency(): String? {
        return currency
    }

    fun setCurrency(currency: String?) {
        this.currency = currency
    }

    private var min_order_amount: String? = null

    fun getMin_order_amount(): String? {
        return min_order_amount
    }

    fun setMin_order_amount(min_order_amount: String?) {
        this.min_order_amount = min_order_amount
    }

    private var max_order_amount: String? = null

    fun getMax_order_amount(): String? {
        return max_order_amount
    }

    fun setMax_order_amount(max_order_amount: String?) {
        this.max_order_amount = max_order_amount
    }

    private var max_order_qty: String? = null

    fun getMax_order_qty(): String? {
        return max_order_qty
    }

    fun setMax_order_qty(max_order_qty: String?) {
        this.max_order_qty = max_order_qty
    }

    private var referral_amount: String? = null

    fun getReferral_amount(): String? {
        return referral_amount
    }

    fun setReferral_amount(referral_amount: String?) {
        this.referral_amount = referral_amount
    }

    private var map: String? = null

    fun getMap(): String? {
        return map
    }

    fun setMap(map: String?) {
        this.map = map
    }

}