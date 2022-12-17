package com.foodapp.app.model

class FoodItemImageModel {
    private var image: String? = null
    private var item_id: String? = null
    private var id: String? = null
    fun getImage(): String? {
        return image
    }

    fun setImage(image: String?) {
        this.image = image
    }

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }
}