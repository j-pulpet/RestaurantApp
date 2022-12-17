package com.foodapp.app.model

class IngredientsModel {
    private var item_id: String? = null

    private var ingredients_image: String? = null

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }

    fun getIngredients_image(): String? {
        return ingredients_image
    }

    fun setIngredients_image(ingredients_image: String?) {
        this.ingredients_image = ingredients_image
    }

}