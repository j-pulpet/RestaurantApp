package com.foodapp.app.model

class AddonsModel {
    private var item_id: String? = null

    private var price: String? = null

    private var name: String? = null

    private var id: String? = null

    private var isAddosSelect:Boolean=false

    private var created_at: String? = null

    private var updated_at: String? = null

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }

    fun getPrice(): String? {
        return price
    }

    fun setPrice(price: String?) {
        this.price = price
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
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

    fun getUpdated_at(): String? {
        return updated_at
    }

    fun setUpdated_at(updated_at: String?) {
        this.updated_at = updated_at
    }

    fun getCreated_at(): String? {
        return created_at
    }

    fun setCreated_at(created_at: String?) {
        this.created_at = created_at
    }

}