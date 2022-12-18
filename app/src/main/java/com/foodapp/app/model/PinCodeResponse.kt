package com.grocery.app.model

import com.google.gson.annotations.SerializedName


data class PinCodeResponse(

        @field:SerializedName("pincode")
        val pincode: String? = null
)
