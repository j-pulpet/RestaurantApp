package com.foodapp.app.api

import com.foodapp.app.model.OrderDetailModel
import com.foodapp.app.model.SummaryModel

class RestOrderDetailResponse {
    private var data: ArrayList<OrderDetailModel>? = null

    private var message: String? = null

    private var status: String? = null

    private var summery: SummaryModel? = null

    private var order_number: String? = null

    private var address: String? = null

    private var order_type: String? = null

    private var landmark: String? = null

    private var building: String? = null

    private var pincode: String? = null

    fun getData(): ArrayList<OrderDetailModel> {
        return data!!
    }

    fun getMessage(): String? {
        return message
    }

    fun getStatus(): String? {
        return status
    }

    fun getSummery(): SummaryModel? {
        return summery
    }

    fun getAddress(): String? {
        return address
    }

    fun getOrder_number(): String? {
        return order_number
    }

    fun getOrder_type(): String? {
        return order_type
    }

    fun getBuilding(): String? {
        return building
    }

    fun getLandmark(): String? {
        return landmark
    }

    fun getPincode(): String? {
        return pincode
    }
}