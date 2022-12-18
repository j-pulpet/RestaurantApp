package com.foodapp.app.model

class CartItemModel {

    private var itemimage: FoodItemImageModel? = null

    private var item_id: String? = null

    private var price: String? = null

    private var qty: String? = null

    private var item_name: String? = null

    private var id: String? = null

    private var addons_id: String? = null

    private var addons_name: String? = null

    private var addons_price: String? = null

    private var item_notes: String? = null

    private var item_image: String? = null
    private var variation: String? = null

    private var addons: ArrayList<AddonsModel>? = null

    fun getItemImage(): String? {
        return item_image
    }

    fun getVariation(): String? {
        return variation
    }

    fun getItem_id(): String? {
        return item_id
    }

    fun getAddOnsPrice(): String? {
        return addons_price
    }

    fun getAddOnsName(): String? {
        return addons_name
    }

    fun getPrice(): String? {
        return price
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