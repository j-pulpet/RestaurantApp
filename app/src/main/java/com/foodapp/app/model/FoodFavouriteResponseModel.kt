package com.foodapp.app.model

class FoodFavouriteResponseModel {
    private var first_page_url: String? = null

    private var path: String? = null

    private var per_page: String? = null

    private var total: String? = null

    private var last_page: String? = null

    private var last_page_url: String? = null

    private var from: String? = null

    private var data: ArrayList<FavouriteFoodModel>?=null

    private var to: String? = null

    private var current_page: String? = null

    fun getFirst_page_url(): String? {
        return first_page_url
    }

    fun setFirst_page_url(first_page_url: String?) {
        this.first_page_url = first_page_url
    }

    fun getPath(): String? {
        return path
    }

    fun setPath(path: String?) {
        this.path = path
    }

    fun getPer_page(): String? {
        return per_page
    }

    fun setPer_page(per_page: String?) {
        this.per_page = per_page
    }

    fun getTotal(): String? {
        return total
    }

    fun setTotal(total: String?) {
        this.total = total
    }

    fun getLast_page(): String? {
        return last_page
    }

    fun setLast_page(last_page: String?) {
        this.last_page = last_page
    }

    fun getLast_page_url(): String? {
        return last_page_url
    }

    fun setLast_page_url(last_page_url: String?) {
        this.last_page_url = last_page_url
    }

    fun getFrom(): String? {
        return from
    }

    fun setFrom(from: String?) {
        this.from = from
    }

    fun getData(): ArrayList<FavouriteFoodModel>? {
        return data
    }

    fun setData(data: ArrayList<FavouriteFoodModel>) {
        this.data = data
    }

    fun getTo(): String? {
        return to
    }

    fun setTo(to: String?) {
        this.to = to
    }

    fun getCurrent_page(): String? {
        return current_page
    }

    fun setCurrent_page(current_page: String?) {
        this.current_page = current_page
    }

}