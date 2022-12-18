package com.foodapp.app.model

class GetPromocodeModel {
    private var offer_amount: String? = null

    private var offer_code: String? = null

    fun getOffer_amount(): String? {
        return offer_amount
    }

    fun getOffer_code(): String? {
        return offer_code
    }
}