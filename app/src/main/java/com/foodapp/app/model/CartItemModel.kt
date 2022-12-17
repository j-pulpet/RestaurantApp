package com.foodapp.app.model

class CartItemModel {
    private var itemimage: FoodItemImageModel? = null
    private var item_id: String? = null
    private var price: String? = null
    private var qty: String? = null
    private var item_name: String? = null
    private var id: String? = null
    private var addons_id: String? = null
    private var item_notes: String? = null
    private var addons: ArrayList<AddonsModel>?=null

    fun getItemimage(): FoodItemImageModel? {
        return itemimage
    }

    fun setItemimage(itemimage: FoodItemImageModel?) {
        this.itemimage = itemimage
    }

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }

    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String?) {
        this.price = price
    }

    fun getQty(): String? {
        return qty
    }

    fun setQty(qty: String?) {
        this.qty = qty
    }

    fun getItem_name(): String? {
        return item_name
    }

    fun setItem_name(item_name: String?) {
        this.item_name = item_name
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

    fun setItem_notes(item_notes: String?) {
        this.item_notes = item_notes
    }

    fun getAddons(): ArrayList<AddonsModel> {
        return addons!!
    }

    fun setAddons(addons: ArrayList<AddonsModel>?) {
        this.addons = addons
    }

    fun getAddons_id(): String? {
        return addons_id
    }

    fun setAddons_id(addons_id: String?) {
        this.addons_id = addons_id
    }

}