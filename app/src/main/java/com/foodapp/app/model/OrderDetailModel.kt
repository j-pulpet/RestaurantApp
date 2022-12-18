package com.foodapp.app.model

import com.google.gson.annotations.SerializedName

class OrderDetailModel {

    private var total_price: String? = null

    private var qty: String? = null

    private var item_name: String? = null

    private var id: String? = null

    private var item_notes: String? = null

    private var addons_id: String? = null

    private var item_image: String? = null

    private var addons_name: String? = null

    private var addons_price: String? = null
    private var variation: String? = null

    fun getAddOnsPrice(): String? {
        return addons_price
    }

    fun getVariation(): String? {
        return variation
    }
    fun getAddOnsName(): String? {
        return addons_name
    }

    fun getTotal_price(): String? {
        return total_price
    }

    fun getItemImage(): String? {
        return item_image
    }

    fun getQty(): String? {
        return qty
    }

    fun getItem_name(): String? {
        return item_name
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getItem_notes(): String? {
        return item_notes
    }

    fun getAddons_id(): String? {
        return addons_id
    }
}