package com.foodapp.app.model

class FoodCategoryModel {
    private var image: String? = null

    private var category_name: String? = null

    private var id: String? = null

    private var isSelect:Boolean?=false

    fun getImage(): String? {
        return image
    }

    fun getCategory_name(): String? {
        return category_name
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun isSelect(): Boolean? {
        return isSelect
    }

    fun setSelect(isSelect: Boolean?) {
        this.isSelect = isSelect
    }

}