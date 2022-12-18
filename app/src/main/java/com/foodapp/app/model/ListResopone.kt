package com.grocery.app.api

import com.foodapp.app.model.SummaryModel
import com.google.gson.annotations.SerializedName

data class ListResopone<T>(
        @field:SerializedName("data")
        val data: ArrayList<T>? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("currency")
        val currency: String? = null,

        @field:SerializedName("order_type")
        val orderType: Int? = null,

        @field:SerializedName("order_number")
        val orderNumber: String? = null,

        @field:SerializedName("pincode")
        val pincode: String? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("landmark")
        val landmark: String? = null,

        @field:SerializedName("building")
        val building: String? = null,

        @field:SerializedName("summery")
        val summery: SummaryModel? = null,

        @field:SerializedName("walletamount")
        val walletamount: String? = null
)