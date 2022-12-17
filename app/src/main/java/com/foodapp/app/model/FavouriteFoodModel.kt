package com.foodapp.app.model

class FavouriteFoodModel {
    private var itemimage: FoodItemImageModel? = null

    private var item_price: String? = null

    private var item_name: String? = null

    private var favorite_id: String? = null

    private var id: String? = null

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

    fun getFavorite_id(): String? {
        return favorite_id
    }

    fun setFavorite_id(favorite_id: String?) {
        this.favorite_id = favorite_id
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }
}