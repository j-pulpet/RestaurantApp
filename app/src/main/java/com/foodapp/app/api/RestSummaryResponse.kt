package com.foodapp.app.api
import com.foodapp.app.model.OrderSummaryModel
import com.foodapp.app.model.SummaryModel
class RestSummaryResponse {

    private var data:ArrayList<OrderSummaryModel>?=null

    private var message: String? = null

    private var status: String? = null

    private var summery:SummaryModel? = null

    fun getData():ArrayList<OrderSummaryModel> {
        return data!!
    }

    fun setData(data:ArrayList<OrderSummaryModel>) {
        this.data = data
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getSummery(): SummaryModel? {
        return summery
    }

    fun setSummery(summery: SummaryModel?) {
        this.summery = summery
    }
}