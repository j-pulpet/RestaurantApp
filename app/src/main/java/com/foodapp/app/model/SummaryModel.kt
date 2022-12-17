package com.foodapp.app.model

class SummaryModel {
    private var delivery_charge: String? = null

    private var discount_amount: String? = null

    private var promocode: String? = null

    private var order_total: String? = null

    private var tax: String? = null

    private var order_notes: String? = null

    private var driver_name: String? = null

    private var driver_mobile: String? = null

    private var driver_profile_image: String? = null

    fun getDelivery_charge(): String? {
        return delivery_charge
    }

    fun setDelivery_charge(delivery_charge: String?) {
        this.delivery_charge = delivery_charge
    }

    fun getDiscount_amount(): String? {
        return discount_amount
    }

    fun setDiscount_amount(discount_amount: String?) {
        this.discount_amount = discount_amount
    }

    fun getPromocode(): String? {
        return promocode
    }

    fun setPromocode(promocode: String?) {
        this.promocode = promocode
    }

    fun getOrder_total(): String? {
        return order_total
    }

    fun setOrder_total(order_total: String?) {
        this.order_total = order_total
    }

    fun getTax(): String? {
        return tax
    }

    fun setTax(tax: String?) {
        this.tax = tax
    }

    fun getOrder_notes(): String? {
        return order_notes
    }

    fun setOrder_notes(order_notes: String?) {
        this.order_notes = order_notes
    }

    fun getDriver_name(): String? {
        return driver_name
    }

    fun setDriver_name(driver_name: String?) {
        this.driver_name = driver_name
    }

    fun getDriver_profile_image(): String? {
        return driver_profile_image
    }

    fun setDriver_profile_image(driver_profile_image: String?) {
        this.driver_profile_image = driver_profile_image
    }

    fun getDriver_mobile(): String? {
        return driver_mobile
    }

    fun setDriver_mobile(driver_mobile: String?) {
        this.driver_mobile = driver_mobile
    }

}