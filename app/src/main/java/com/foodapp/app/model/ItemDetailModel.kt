package com.foodapp.app.model

class ItemDetailModel {

    private var images: ArrayList<ImagesModel>?=null

    private var category_name: String? = null

    private var item_price: String? = null

    private var ingredients: ArrayList<IngredientsModel>?=null

    private var addons: ArrayList<AddonsModel>?=null

    private var item_name: String? = null

    private var id: String? = null

    private var delivery_time: String? = null

    private var item_description: String? = null

    private var item_status: String? = null


    fun getImages(): ArrayList<ImagesModel>? {
        return images
    }

    fun setImages(images: ArrayList<ImagesModel>?) {
        this.images = images
    }

    fun getCategory_name(): String? {
        return category_name
    }

    fun setCategory_name(category_name: String?) {
        this.category_name = category_name
    }

    fun getItem_price(): String? {
        return item_price
    }

    fun setItem_price(item_price: String?) {
        this.item_price = item_price
    }

    fun getIngredients(): ArrayList<IngredientsModel>? {
        return ingredients
    }

    fun setIngredients(ingredients: ArrayList<IngredientsModel>?) {
        this.ingredients = ingredients
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

    fun getDelivery_time(): String? {
        return delivery_time
    }

    fun setDelivery_time(delivery_time: String?) {
        this.delivery_time = delivery_time
    }

    fun getItem_description(): String? {
        return item_description
    }

    fun setItem_description(item_description: String?) {
        this.item_description = item_description
    }

    fun getAddons(): ArrayList<AddonsModel> {
        return addons!!
    }

    fun setAddons(addons: ArrayList<AddonsModel>?) {
        this.addons = addons
    }

    fun getItem_status(): String? {
        return item_status
    }

    fun setItem_status(item_status: String?) {
        this.item_status = item_status
    }
}