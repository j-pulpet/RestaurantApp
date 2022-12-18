package com.foodapp.app.model

class FoodFavouriteResponseModel {

    private var last_page: String? = null

    private var data: ArrayList<FavouriteFoodModel>? = null

    private var current_page: String? = null

    fun getLast_page(): String? {
        return last_page
    }

    fun getData(): ArrayList<FavouriteFoodModel>? {
        return data
    }

    fun getCurrent_page(): String? {
        return current_page
    }
}