package com.foodapp.app.model

import com.google.gson.annotations.SerializedName

data class SearchItemModel(

	@field:SerializedName("item_name")
	val itemName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
