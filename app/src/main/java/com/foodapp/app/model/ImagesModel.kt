package com.foodapp.app.model

class ImagesModel {
    private var itemimage: String? = null

    private var item_id: String? = null

    fun getItemimage(): String? {
        return itemimage
    }

    fun setItemimage(itemimage: String?) {
        this.itemimage = itemimage
    }

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }
}