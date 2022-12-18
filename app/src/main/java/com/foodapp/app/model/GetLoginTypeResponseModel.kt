package com.foodapp.app.model

import com.google.gson.annotations.SerializedName

data class GetLoginTypeResponseModel(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
