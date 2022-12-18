package com.foodapp.app.model

import com.google.gson.annotations.SerializedName

data class WalletModel(
        @field:SerializedName("date")
        val date: String? = null,

        @field:SerializedName("wallet")
        val wallet: String? = null,

        @field:SerializedName("order_number")
        val orderNumber: String? = null,

        @field:SerializedName("transaction_type")
        val transactionType: String? = null,

        @field:SerializedName("username")
        val username: String? = null,

        @field:SerializedName("order_type")
        val order_type: String? = null,
)
