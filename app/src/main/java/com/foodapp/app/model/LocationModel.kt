package com.foodapp.app.model

class LocationModel {
    private var lang: String? = null

    private var lat: String? = null

    fun getLang(): String? {
        return lang
    }

    fun setLang(lang: String?) {
        this.lang = lang
    }

    fun getLat(): String? {
        return lat
    }

    fun setLat(lat: String?) {
        this.lat = lat
    }
}