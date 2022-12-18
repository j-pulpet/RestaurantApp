package com.foodapp.app.model

class AddonsModel {
    private var item_id: String? = null

    private var price: String? = null

    private var name: String? = null

    private var id: String? = null

    private var isAddosSelect: Boolean = false

    private var created_at: String? = null

    private var updated_at: String? = null

    constructor(price: String?, name: String?, id: String?) {
        this.price = price
        this.name = name
        this.id = id
    }

    fun getPrice(): String? {
        return price
    }

    fun getName(): String? {
        return name
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun isSelectAddons(): Boolean? {
        return isAddosSelect
    }

    fun setSelectAddons(isAddosSelect: Boolean?) {
        this.isAddosSelect = isAddosSelect!!
    }
}