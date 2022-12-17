package com.foodapp.app.model

class RattingModel {
    private var ratting: String? = null

    private var name: String? = null

    private var created_at: String? = null

    private var comment: String? = null

    fun getRatting(): String? {
        return ratting
    }

    fun setRatting(ratting: String?) {
        this.ratting = ratting
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getCreated_at(): String? {
        return created_at
    }

    fun setCreated_at(created_at: String?) {
        this.created_at = created_at
    }

    fun getComment(): String? {
        return comment
    }

    fun setComment(comment: String?) {
        this.comment = comment
    }
}