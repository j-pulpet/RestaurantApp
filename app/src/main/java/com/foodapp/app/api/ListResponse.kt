package com.foodapp.app.api

import com.google.gson.annotations.SerializedName

data class ListResponse<T>(
        @field:SerializedName("data")
        var data: ArrayList<T>? = null,

        @field:SerializedName("message")
        var message: String? = null,

        @field:SerializedName("status")
        var status: Int = 0,

        @field:SerializedName("walletamount")
        val walletamount: String? = null
)