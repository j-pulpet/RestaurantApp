package com.foodapp.app.model

class BannerModel {
    private var image: String? = null

    private var id: String? = null

    private var item_id: String? = null

    private var cat_id: String? = null

    private var category_name: String? = null
    private var type: String? = null

    fun getImage(): String? {
        return image
    }

    fun getCategoryName(): String? {
        return category_name
    }

    fun getId(): String? {
        return id
    }

    fun getItemId(): String? {
        return item_id
    }
    fun getType():String?{
        return type
    }

    fun getCatId(): String? {
        return cat_id
    }

    fun setId(id: String?) {
        this.id = id
    }
}