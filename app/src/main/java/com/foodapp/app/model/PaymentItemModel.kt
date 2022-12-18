package com.foodapp.app.model

import com.google.gson.annotations.SerializedName

data class PaymentItemModel(

        @field:SerializedName("environment")
        var environment: Int? = null,

        @field:SerializedName("live_public_key")
        var livePublicKey: String? = null,

        @field:SerializedName("test_public_key")
        var testPublicKey: String? = null,

        @field:SerializedName("payment_name")
        var paymentName: String? = null,

        @field:SerializedName("wallet_amount")
        var wallet_amount: String? = null,

        @field:SerializedName("isSelect")
        var isSelect: Boolean = false
)
