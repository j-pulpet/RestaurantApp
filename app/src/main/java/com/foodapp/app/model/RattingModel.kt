package com.foodapp.app.model

class RattingModel {
    private var ratting: String? = null

    private var name: String? = null

    private var created_at: String? = null

    private var comment: String? = null

    fun getRatting(): String? {
        return ratting
    }

    fun getName(): String? {
        return name
    }

    fun getCreated_at(): String? {
        return created_at
    }

    fun getComment(): String? {
        return comment
    }
}