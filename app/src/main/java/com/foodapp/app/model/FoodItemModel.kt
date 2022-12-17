package com.foodapp.app.model

class FoodItemModel {
    private var itemimage: FoodItemImageModel? = null

    private var item_price: String? = null

    private var item_name: String? = null

    private var id: String? = null

    private var is_favorite: String? = null

    fun getItemimage(): FoodItemImageModel? {
        return itemimage
    }

    fun setItemimage(itemimage: FoodItemImageModel?) {
        this.itemimage = itemimage
    }

    fun getItem_price(): String? {
        return item_price
    }

    fun setItem_price(item_price: String?) {
        this.item_price = item_price
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

    fun getIs_favorite(): String? {
        return is_favorite
    }

    fun setIs_favorite(is_favorite: String?) {
        this.is_favorite = is_favorite
    }

}