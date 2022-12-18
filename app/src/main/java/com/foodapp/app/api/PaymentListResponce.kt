package com.foodapp.app.api

import com.foodapp.app.model.PaymentItemModel
import com.google.gson.annotations.SerializedName

data class PaymentListResponce(
        @field:SerializedName("payment")
        val data: ArrayList<PaymentItemModel>? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("walletamount")
        val walletamount: String? = null,

        @field:SerializedName("logo")
        val logo: String? = null
)
