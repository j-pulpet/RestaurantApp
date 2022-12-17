package com.foodapp.app.model

class OrderHistoryModel {
    private var payment_type: String? = null

    private var address: String? = null

    private var total_price: String? = null

    private var qty: String? = null

    private var order_number: String? = null

    private var id: String? = null

    private var status: String? = null

    private var date: String? = null

    private var isCheck:Boolean=false

    private var order_type: String? = null

    fun getPayment_type(): String? {
        return payment_type
    }

    fun setPayment_type(payment_type: String?) {
        this.payment_type = payment_type
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getTotal_price(): String? {
        return total_price
    }

    fun setTotal_price(total_price: String?) {
        this.total_price = total_price
    }

    fun getQty(): String? {
        return qty
    }

    fun setQty(qty: String?) {
        this.qty = qty
    }

    fun getOrder_number(): String? {
        return order_number
    }

    fun setOrder_number(order_number: String?) {
        this.order_number = order_number
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun isCheck(): Boolean? {
        return isCheck
    }

    fun setCheck(isCheck: Boolean?) {
        this.isCheck = isCheck!!
    }
    fun getOrder_type(): String? {
        return order_type
    }

    fun setOrder_type(order_type: String?) {
        this.order_type = order_type
    }

}