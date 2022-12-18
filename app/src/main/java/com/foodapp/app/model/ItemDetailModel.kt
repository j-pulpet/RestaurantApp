package com.foodapp.app.model

class ItemDetailModel {

    private var images: ArrayList<ImagesModel>? = null

    private var category_name: String? = null

    private var item_price: String? = null

    private var ingredients: ArrayList<IngredientsModel>? = null

    private var addons: ArrayList<AddonsModel>? = null

    private var item_name: String? = null

    private  var id: String? = null
    private var tax: String? = null

    private var delivery_time: String? = null

    private var item_description: String? = null

    private var item_status: String? = null

    private var variation: ArrayList<VariationItem>? = null
    fun getImages(): ArrayList<ImagesModel>? {
        return images
    }

    fun getCategory_name(): String? {
        return category_name
    }

    fun getTax():String?{
        return tax
    }
    fun getVariation(): ArrayList<VariationItem>? {
        return variation
    }

    fun getItem_price(): String? {
        return item_price
    }

    fun getIngredients(): ArrayList<IngredientsModel>? {
        return ingredients
    }

    fun getItem_name(): String? {
        return item_name
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

    fun getItem_description(): String? {
        return item_description
    }

    fun getAddons(): ArrayList<AddonsModel> {
        return addons!!
    }

    fun getItem_status(): String? {
        return item_status
    }
}