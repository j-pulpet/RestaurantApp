package com.foodapp.app.model

class PromocodeModel {
    private var offer_amount: String? = null

    private var description: String? = null

    private var offer_code: String? = null

    private var offer_name: String? = null

    fun getOffer_amount(): String? {
        return offer_amount
    }

    fun getDescription(): String? {
        return description
    }

    fun getOffer_code(): String? {
        return offer_code
    }

    fun getOffer_name(): String? {
        return offer_name
    }
}