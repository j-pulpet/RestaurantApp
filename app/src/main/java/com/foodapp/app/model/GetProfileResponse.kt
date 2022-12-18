package com.foodapp.app.model

import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
        @field:SerializedName("data")
        val data: UserData? = null,

        @field:SerializedName("admin")
        val admin: Admin? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("status")
        val status: Int? = null
)

data class Admin(
        @field:SerializedName("insta")
        val insta: String? = null,

        @field:SerializedName("twitter")
        val twitter: String? = null,

        @field:SerializedName("address")
        val address: String? = null,

        @field:SerializedName("mobile")
        val mobile: String? = null,

        @field:SerializedName("fb")
        val fb: String? = null,

        @field:SerializedName("email")
        val email: String? = null
)

data class UserData(

        @field:SerializedName("profile_image")
        val profileImage: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("mobile")
        val mobile: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("email")
        val email: String? = null
)
