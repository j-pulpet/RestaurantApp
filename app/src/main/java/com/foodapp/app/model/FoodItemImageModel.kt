package com.foodapp.app.model

class FoodItemImageModel {
    private var image: String? = null

    private var id: String? = null

    fun getImage(): String? {
        return image
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }
}