package com.foodapp.app.model

class OrderDetailModel {
    private var itemimage: FoodItemImageModel? = null

    private var total_price: String? = null

    private var item_id: String? = null

    private var item_price: String? = null

    private var qty: String? = null

    private var item_name: String? = null

    private var id: String? = null

    private var item_notes: String? = null

    private var addons: ArrayList<AddonsModel>?=null

    private var addons_id: String? = null

    private var landmark: String? = null

    private var building: String? = null


    fun getItemimage(): FoodItemImageModel {
        return itemimage!!
    }

    fun setItemimage(itemimage: FoodItemImageModel) {
        this.itemimage = itemimage
    }

    fun getTotal_price(): String? {
        return total_price
    }

    fun setTotal_price(total_price: String?) {
        this.total_price = total_price
    }

    fun getItem_id(): String? {
        return item_id
    }

    fun setItem_id(item_id: String?) {
        this.item_id = item_id
    }

    fun getItem_price(): String? {
        return item_price
    }

    fun setItem_price(item_price: String?) {
        this.item_price = item_price
    }

    fun getQty(): String? {
        return qty
    }

    fun setQty(qty: String?) {
        this.qty = qty
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

    fun getItem_notes(): String? {
        return item_notes
    }

    fun setItem_notes(item_notes: String?) {
        this.item_notes = item_notes
    }

    fun getAddons(): ArrayList<AddonsModel> {
        return addons!!
    }

    fun setAddons(addons: ArrayList<AddonsModel>?) {
        this.addons = addons
    }

    fun getAddons_id(): String? {
        return addons_id
    }

    fun setAddons_id(addons_id: String?) {
        this.addons_id = addons_id
    }


    fun getBuilding(): String? {
        return building
    }

    fun setBuilding(building: String?) {
        this.building = building
    }

    fun getLandmark(): String? {
        return landmark
    }

    fun setLandmark(landmark: String?) {
        this.landmark = landmark
    }

}