package com.foodapp.app.model

import com.google.gson.annotations.SerializedName

data class AddressResponse(

        @field:SerializedName("pincode")
        val pincode: String? = null,
        @field:SerializedName("delivery_charge")
        val deliveryCharge: String? = null,

        @field:SerializedName("full_name")
        val fullName: String? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("address_type")
        val addressType: Int? = null,

        @field:SerializedName("user_id")
        val userId: Int? = null,

        @field:SerializedName("mobile")
        val mobile: String? = null,

        @field:SerializedName("lat")
        val lat: String? = null,

        @field:SerializedName("lang")
        val lang: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("landmark")
        val landmark: String? = null,

        @field:SerializedName("building")
        val building: String? = null
)
