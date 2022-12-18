package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodapp.app.R
import com.foodapp.app.api.*
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.FieldSelector
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.isCurrancy
import com.foodapp.app.utils.SharePreference.Companion.userId
import com.bumptech.glide.Glide
import com.foodapp.app.model.*
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.isMaximum
import com.foodapp.app.utils.SharePreference.Companion.isMiniMum
import com.foodapp.app.utils.SharePreference.Companion.mapKey
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_yoursorderdetail.*
import kotlinx.android.synthetic.main.activity_yoursorderdetail.ivBack
import kotlinx.android.synthetic.main.row_orderitemsummary.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OrderSummuryActivity : BaseActivity() {
    var summaryModel = SummaryModel()
    var promocodeList: ArrayList<PromocodeModel>? = null
    var discountAmount = "0.00"
    var discountPer = "0"
    var promocodePrice: Float = 0.0F
    var lat: Double = 0.0
    var lon: Double = 0.0
    var select_Delivery = 1
    var deliveryCharge="0.00"
    private var AUTOCOMPLETE_REQUEST_CODE: Int = 2
    var fieldSelector: FieldSelector? = null
    override fun setLayout(): Int {
        return R.layout.activity_yoursorderdetail
    }

    @SuppressLint("SetTextI18n")
    override fun InitView() {
        promocodeList = ArrayList()
        val mapKey = getStringPref(this@OrderSummuryActivity, mapKey)
        mapKey?.let { Places.initialize(applicationContext, it) }
        fieldSelector = FieldSelector()
        rlOffer.visibility = View.GONE
        if (Common.isCheckNetwork(this@OrderSummuryActivity)) {
            callApiOrderSummary()
        } else {
            alertErrorOrValidationDialog(
                    this@OrderSummuryActivity,
                    resources.getString(R.string.no_internet)
            )
        }
        if (getStringPref(this@OrderSummuryActivity, SharePreference.SELECTED_LANGUAGE).equals(
                        resources.getString(R.string.language_hindi)
                )
        ) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }

        tvSelectAddress?.setOnClickListener {
            val intent = Intent(this@OrderSummuryActivity, GetAddressActivity::class.java)
            intent.putExtra("isComeFromSelectAddress", true)
            startActivityForResult(intent, 500)
        }

        edAddress.setOnClickListener {
            getLocation()
        }
        cvPickup.setOnClickListener {
            select_Delivery = 2
            cvPickup.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))
            cvDelivery.setCardBackgroundColor(resources.getColor(R.color.white))
            tvTitle.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.black,null)))
            tvPickupTitle.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.white,null)))
            cvDeliveryAddress.visibility = View.GONE
            if (tvApply.text.toString() == resources.getString(R.string.remove)) {
                tvDiscountOffer.text = "-$discountPer%"
                val subtotalCharge =
                        (summaryModel.getOrder_total()!!.toFloat() * discountPer.toFloat()) / 100
                val total = summaryModel.getOrder_total()!!.toFloat() - subtotalCharge
                val ordreTax = (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                        .toFloat()) / 100
                val mainTotal = ordreTax + total + 0.00
                tvOrderDeliveryCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + "0.00"
                tvDiscountOffer.text =
                        "-" + getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                subtotalCharge
                        )
                tvOrderTotalCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                mainTotal
                        )
            } else {
                val orderTax: Float =
                        (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                .toFloat()) / 100.toFloat()
                tvOrderTotalPrice.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                summaryModel.getOrder_total()!!.toDouble()
                        )
                tvOrderTaxPrice.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                summaryModel.getTax()!!.toFloat()
                        )
//                tvTitleTex.text = "Tax (${summaryModel.getTax()}%)""
                tvTitleTex.text = "Tax"
                tvOrderDeliveryCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + "0.00"
                val totalprice = summaryModel.getOrder_total()!!.toFloat() + summaryModel.getTax()?.toFloat()!! + 0.00
                tvOrderTotalCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                totalprice
                        )
            }
        }
        cvDelivery.setOnClickListener {
            cvDeliveryAddress.visibility = View.VISIBLE
            select_Delivery = 1
            cvPickup.setCardBackgroundColor(resources.getColor(R.color.white))
            cvDelivery.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))
            tvTitle.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.white,null)))
            tvPickupTitle.setTextColor(ColorStateList.valueOf(ResourcesCompat.getColor(resources,R.color.black,null)))
            if (tvApply.text.toString().equals(resources.getString(R.string.remove))) {
                "-$discountPer%".also { tvDiscountOffer.text = it }
                val subtotalCharge =
                        (summaryModel.getOrder_total()!!.toFloat() * discountPer.toFloat()) / 100
                val total = summaryModel.getOrder_total()!!.toFloat() - subtotalCharge
                val ordreTax = (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                        .toFloat()) / 100
                val mainTotal = summaryModel.getTax()!!.toFloat() + total + deliveryCharge.toFloat()
                tvOrderDeliveryCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                deliveryCharge.toDouble()
                        )
                tvDiscountOffer.text =
                        "-" + getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                subtotalCharge
                        )
                tvOrderTotalCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                mainTotal
                        )
            } else {
                val orderTax: Float =
                        (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                .toFloat()) / 100.toFloat()
                tvOrderTotalPrice.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                summaryModel.getOrder_total()!!.toDouble()
                        )
                tvOrderTaxPrice.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                summaryModel.getTax()!!.toFloat()
                        )
//                tvTitleTex.text = "Tax (${summaryModel.getTax()}%)"
                tvTitleTex.text = "Tax"
                tvOrderDeliveryCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                deliveryCharge.toDouble()
                        )
                val totalprice = summaryModel.getOrder_total()!!
                        .toFloat() + summaryModel.getTax()?.toFloat()!! + deliveryCharge.toFloat()
                tvOrderTotalCharge.text =
                        getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                totalprice
                        )
            }


        }

        tvProceedToPaymnet.setOnClickListener {
            if (select_Delivery == 1) {
                if (edAddress.text.toString() == "") {
                    alertErrorOrValidationDialog(
                            this@OrderSummuryActivity,
                            resources.getString(R.string.address_error)
                    )
                } else {

                    if (summaryModel.getOrder_total()!!.toDouble() >= getStringPref(
                                    this@OrderSummuryActivity,
                                    isMiniMum
                            )!!.toDouble() && summaryModel.getOrder_total()!!
                                    .toDouble() <= getStringPref(
                                    this@OrderSummuryActivity,
                                    isMaximum
                            )!!.toDouble()
                    ) {
                        val intent =
                                Intent(this@OrderSummuryActivity, PaymentPayActivity::class.java)
                        val strTotalCharge = tvOrderTotalCharge.text.toString()
                                .replace(getStringPref(this@OrderSummuryActivity, isCurrancy)!!, "")
                        val strActuleCharge = strTotalCharge.replace(",", "")
                        val orderTax: Float =
                                (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                        .toFloat()) / 100
                        intent.putExtra(
                                "getAmount",
                                String.format(Locale.US, "%.2f", strActuleCharge.toDouble())
                        )
                        intent.putExtra("getAddress", edAddress.text.toString())
                        intent.putExtra("getTax", summaryModel.getTax())
                        intent.putExtra(
                                "getTaxAmount",
                                String.format(Locale.US, "%.2f", summaryModel.getTax()!!.toFloat())
                        )
                        intent.putExtra(
                                "delivery_charge",
                                String.format(
                                        Locale.US,
                                        "%.2f",
                                        deliveryCharge!!.toDouble()
                                )
                        )
                        intent.putExtra("promocode", tvPromoCodeApply.text.toString())
                        intent.putExtra("discount_pr", discountPer)
                        intent.putExtra("discount_amount", discountAmount)
                        intent.putExtra("order_notes", edNotes.text.toString())
                        intent.putExtra("lat", lat.toString())
                        intent.putExtra("lon", lon.toString())
                        intent.putExtra("order_type", "1")
                        intent.putExtra("building", edBuilding.text.toString())
                        intent.putExtra("landmark", edLandmark.text.toString())
                        intent.putExtra("pincode", edPinCode.text.toString())
                        startActivity(intent)
                    } else {
                        alertErrorOrValidationDialog(
                                this@OrderSummuryActivity,
                                "Order amount must be between ${
                                getStringPref(
                                        this@OrderSummuryActivity,
                                        isCurrancy
                                ) + getStringPref(this@OrderSummuryActivity, isMiniMum)
                                } and ${
                                getStringPref(
                                        this@OrderSummuryActivity,
                                        isCurrancy
                                ) + getStringPref(this@OrderSummuryActivity, isMaximum)
                                }"
                        )
                    }
                }
            } else if (select_Delivery == 2) {
                if (summaryModel.getOrder_total()!!.toDouble() >= getStringPref(
                                this@OrderSummuryActivity,
                                isMiniMum
                        )!!.toDouble() && summaryModel.getOrder_total()!!.toDouble() <= getStringPref(
                                this@OrderSummuryActivity,
                                isMaximum
                        )!!.toDouble()
                ) {
                    val intent = Intent(this@OrderSummuryActivity, PaymentPayActivity::class.java)
                    val strTotalCharge = tvOrderTotalCharge.text.toString()
                            .replace(getStringPref(this@OrderSummuryActivity, isCurrancy)!!, "")
                    val strActuleCharge = strTotalCharge.replace(",", "")
                    val orderTax: Float =
                            (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                    .toFloat()) / 100
                    intent.putExtra(
                            "getAmount",
                            String.format(Locale.US, "%.2f", strActuleCharge.toDouble())
                    )
                    intent.putExtra("getAddress", "")
                    intent.putExtra("getTax", summaryModel.getTax())
                    intent.putExtra("getTaxAmount", String.format(Locale.US, "%.2f", summaryModel.getTax()!!
                        .toFloat()))
                    intent.putExtra("delivery_charge", "0.00")
                    intent.putExtra("promocode", tvPromoCodeApply.text.toString())
                    intent.putExtra("discount_pr", discountPer)
                    intent.putExtra("discount_amount", discountAmount)
                    intent.putExtra("order_notes", edNotes.text.toString())
                    intent.putExtra("order_type", "2")
                    intent.putExtra("building", "")
                    intent.putExtra("landmark", "")
                    intent.putExtra("pincode", "")
                    intent.putExtra("lat", "")
                    intent.putExtra("lon", "")
                    startActivity(intent)
                } else {
                    alertErrorOrValidationDialog(
                            this@OrderSummuryActivity,
                            "Order amount must be between ${
                            getStringPref(
                                    this@OrderSummuryActivity,
                                    isCurrancy
                            ) + getStringPref(this@OrderSummuryActivity, isMiniMum)
                            } and ${
                            getStringPref(
                                    this@OrderSummuryActivity,
                                    isCurrancy
                            ) + getStringPref(this@OrderSummuryActivity, isMaximum)
                            }"
                    )
                }
            }
        }
        ivBack.setOnClickListener {
            finish()
        }

        tvbtnPromocode.setOnClickListener {
            if (Common.isCheckNetwork(this@OrderSummuryActivity)) {
                callApiPromocode()
            } else {
                alertErrorOrValidationDialog(
                        this@OrderSummuryActivity,
                        resources.getString(R.string.no_internet)
                )
            }
        }

        tvApply.setOnClickListener {
            if (tvApply.text.toString().equals(resources.getString(R.string.apply))) {
                if (!edPromocode.text.toString().equals("")) {
                    callApiCheckPromocode()
                }
            } else if (tvApply.text.toString().equals(resources.getString(R.string.remove))) {
                tvbtnPromocode.visibility = View.VISIBLE
                tvPromoCodeApply.text = ""
                tvDiscountOffer.text = ""
                edPromocode.setText("")
                tvApply.text = resources.getString(R.string.apply)
                rlOffer.visibility = View.GONE
                if (select_Delivery == 1) {
                    val orderTax: Float =
                            (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                    .toFloat()) / 100
                    tvOrderTotalPrice.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(
                            Locale.US,
                            "%,.2f",
                            summaryModel.getOrder_total()!!.toDouble()
                    )
                    tvOrderTaxPrice.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(Locale.US, "%,.2f", summaryModel.getTax()!!.toFloat())
//                    tvTitleTex.text = "Tax (${summaryModel.getTax()}%)"
                    tvTitleTex.text = "Tax"

                    tvOrderDeliveryCharge.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(
                            Locale.US,
                            "%,.2f",
                            deliveryCharge!!.toDouble()
                    )
                    val totalprice = summaryModel.getOrder_total()!!
                            .toDouble() + summaryModel.getTax()?.toDouble()!! + deliveryCharge.toDouble()
                    tvOrderTotalCharge.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(Locale.US, "%,.2f", totalprice)
                    discountPer = "0"
                    discountAmount = "0.00"
                } else {
                    val orderTax: Float =
                            (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                    .toFloat()) / 100
                    tvOrderTotalPrice.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(
                            Locale.US,
                            "%,.2f",
                            summaryModel.getOrder_total()!!.toDouble()
                    )
                    tvOrderTaxPrice.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(Locale.US, "%,.2f", summaryModel.getTax()!!.toFloat())
//                    tvTitleTex.text = "Tax (${summaryModel.getTax()}%)"
                    tvTitleTex.text = "Tax"
                    tvOrderDeliveryCharge.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(Locale.US, "%,.2f", 0.00)
                    val totalprice = summaryModel.getOrder_total()!!.toDouble() + summaryModel.getTax()?.toDouble()!! + 0.00
                    tvOrderTotalCharge.text = getStringPref(
                            this@OrderSummuryActivity,
                            isCurrancy
                    ) + String.format(Locale.US, "%,.2f", totalprice)
                    discountPer = "0"
                    discountAmount = "0.00"
                }

            }
        }

    }

    override fun onBackPressed() {
        finish()
    }

    private fun callApiOrderSummary() {
        Common.showLoadingProgress(this@OrderSummuryActivity)
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@OrderSummuryActivity, userId)!!
        val call = ApiClient.getClient.setSummary(map)
        call.enqueue(object : Callback<RestSummaryResponse> {
            override fun onResponse(
                    call: Call<RestSummaryResponse>,
                    response: Response<RestSummaryResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: RestSummaryResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderItemFood.visibility = View.VISIBLE
                            val foodCategoryList = restResponce.getData()
                            val summary = restResponce.getSummery()
                            setFoodCategoryAdaptor(foodCategoryList, summary)
                        } else {
                            rvOrderItemFood.visibility = View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    val status = error.getInt("status")
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(this@OrderSummuryActivity, error.getString("message"))
                }
            }

            override fun onFailure(call: Call<RestSummaryResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@OrderSummuryActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiPromocode() {
        Common.showLoadingProgress(this@OrderSummuryActivity)
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@OrderSummuryActivity, userId)!!
        val call = ApiClient.getClient.getPromoCodeList()
        call.enqueue(object : Callback<ListResponse<PromocodeModel>> {
            override fun onResponse(
                    call: Call<ListResponse<PromocodeModel>>,
                    response: Response<ListResponse<PromocodeModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<PromocodeModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data!!.size > 0) {
                            promocodeList = restResponce.data
                            openDialogPromocode()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<PromocodeModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@OrderSummuryActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiCheckPromocode() {
        Common.showLoadingProgress(this@OrderSummuryActivity)
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@OrderSummuryActivity, userId)!!
        map["offer_code"] = edPromocode.text.toString()
        val call = ApiClient.getClient.setApplyPromocode(map)
        call.enqueue(object : Callback<RestResponse<GetPromocodeModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                    call: Call<RestResponse<GetPromocodeModel>>,
                    response: Response<RestResponse<GetPromocodeModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: RestResponse<GetPromocodeModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        tvbtnPromocode.visibility = View.GONE
                        rlOffer.visibility = View.VISIBLE
                        tvDiscountOffer.text =
                                "-" + restResponce.getData()!!.getOffer_amount() + "%"
                        tvPromoCodeApply.text = restResponce.getData()!!.getOffer_code()
                        tvApply.text = resources.getString(R.string.remove)
                        if (select_Delivery == 1) {
                            val subtotalCharge = (summaryModel.getOrder_total()!!
                                    .toFloat() * restResponce.getData()!!.getOffer_amount()!!
                                    .toFloat()) / 100
                            val total = summaryModel.getOrder_total()!!.toFloat() - subtotalCharge
                            val ordreTax =
                                    (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                            .toFloat()) / 100
                            val mainTotal =
                                    ordreTax + total + deliveryCharge.toFloat()
                            tvDiscountOffer.text = "-" + getStringPref(
                                    this@OrderSummuryActivity,
                                    isCurrancy
                            ) + String.format(Locale.US, "%,.2f", subtotalCharge)
                            tvOrderTotalCharge.text = getStringPref(
                                    this@OrderSummuryActivity,
                                    isCurrancy
                            ) + String.format(Locale.US, "%,.2f", mainTotal)
                            discountAmount = subtotalCharge.toString()
                            discountPer = restResponce.getData()!!.getOffer_amount()!!
                        } else {
                            val subtotalCharge = (summaryModel.getOrder_total()!!
                                    .toFloat() * restResponce.getData()!!.getOffer_amount()!!
                                    .toFloat()) / 100
                            val total = summaryModel.getOrder_total()!!.toFloat() - subtotalCharge
                            val ordreTax =
                                    (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                            .toFloat()) / 100
                            val mainTotal = ordreTax + total + 0.00
                            tvDiscountOffer.text = "-" + getStringPref(
                                    this@OrderSummuryActivity,
                                    isCurrancy
                            ) + String.format(Locale.US, "%,.2f", subtotalCharge)
                            tvOrderTotalCharge.text = getStringPref(
                                    this@OrderSummuryActivity,
                                    isCurrancy
                            ) + String.format(Locale.US, "%,.2f", mainTotal)
                            discountAmount = subtotalCharge.toString()
                            discountPer = restResponce.getData()!!.getOffer_amount()!!
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        edPromocode.setText("")
                        rlOffer.visibility = View.GONE
                        tvApply.text = resources.getString(R.string.apply)
                        alertErrorOrValidationDialog(
                                this@OrderSummuryActivity,
                                restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<GetPromocodeModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@OrderSummuryActivity,
                        resources.getString(R.string.error_msg)
                )
            }


        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodCategoryAdaptor(
            foodCategoryList: ArrayList<OrderSummaryModel>,
            summary: SummaryModel?
    ) {
        if (foodCategoryList.size > 0) {
            setFoodCategoryAdaptor(foodCategoryList)
        }
        summaryModel = summary!!
        val orderTax: Float = (summary.getOrder_total()!!.toFloat() * summary.getTax()!!.toFloat()) / 100.toFloat()
        tvOrderTotalPrice.text =
                getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.2f",
                        summary.getOrder_total()!!.toDouble()
                )
        tvOrderTaxPrice.text = getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                Locale.US,
                "%,.2f",
                summary.getTax()!!.toFloat()
        )
//        tvTitleTex.text = "Tax (${summary.getTax()}%)"
        tvTitleTex.text = "Tax"
        tvOrderDeliveryCharge.text =
                getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.2f",
                        deliveryCharge.toDouble()
                )
        val totalprice =
                summary.getOrder_total()!!.toFloat() + summary.getTax()?.toFloat()!! + deliveryCharge
                        .toFloat()
        tvOrderTotalCharge.text =
                getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.2f",
                        totalprice
                )
    }

    private fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderSummaryModel>) {
        val orderHistoryAdapter =
                object : BaseAdaptor<OrderSummaryModel>(this@OrderSummuryActivity, orderHistoryList) {
                    @SuppressLint("SetTextI18n", "NewApi", "UseCompatLoadingForDrawables")
                    override fun onBindData(
                            holder: RecyclerView.ViewHolder?,
                            `val`: OrderSummaryModel,
                            position: Int
                    ) {
                        val tvOrderFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                        val ivFoodItem: ImageView = holder.itemView.findViewById(R.id.ivFoodCart)
                        val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                        val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyPrice)
                        val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)
                        val tvNotes: TextView = holder.itemView.findViewById(R.id.tvNotes)
                        val tvAddons: TextView = holder.itemView.findViewById(R.id.tvAddons)

                        Glide.with(this@OrderSummuryActivity)
                                .load(orderHistoryList.get(position).itemImage)
                                .placeholder(resources.getDrawable(R.drawable.ic_placeholder)).centerCrop()
                                .into(ivFoodItem)
                        tvOrderFoodName.text = orderHistoryList.get(position).itemName.toString()
                        tvPrice.text = getStringPref(
                                this@OrderSummuryActivity,
                                isCurrancy
                        ) + String.format(
                                Locale.US,
                                "%,.2f",
                                orderHistoryList.get(position).totalPrice!!.toDouble()
                        )
                        tvQtyNumber.text = "QTY : ${orderHistoryList.get(position).qty}"
                        tvWeight.text= orderHistoryList[position].variation.toString()

                        if (orderHistoryList.get(position).addonsId != "") {
                            tvAddons.backgroundTintList =
                                    ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                            tvAddons.visibility=View.VISIBLE
                        } else {
                            tvAddons.visibility=View.GONE

                            tvAddons.backgroundTintList =
                                    ColorStateList.valueOf(resources.getColor(R.color.gray))
                        }
                        if (orderHistoryList[position].itemNotes == null || orderHistoryList[position].itemNotes == "") {
                            tvNotes.visibility=View.GONE
                            tvNotes.backgroundTintList =
                                    ColorStateList.valueOf(resources.getColor(R.color.gray))
                        } else {
                            tvNotes.visibility=View.VISIBLE

                            tvNotes.backgroundTintList =
                                    ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                        }

                        val getIds = orderHistoryList.get(position).addonsId?.split(",")?.toTypedArray()
                        val getNames =
                                orderHistoryList.get(position).addonsName?.split(",")?.toTypedArray()
                        val getPrice =
                                orderHistoryList.get(position).addonsPrice?.split(",")?.toTypedArray()

                        val addOnList = ArrayList<AddonsModel>()
                        for (i in 0 until getIds?.size!!) {
                            if (getIds[i] != "") {
                                addOnList.add(
                                        AddonsModel(
                                                getPrice?.get(i).toString(),
                                                getNames?.get(i).toString(),
                                                getIds.get(i).toString()
                                        )
                                )
                            }
                        }

                        holder.itemView.tvAddons.setOnClickListener {
                            if (addOnList.size > 0) {
                                Common.openDialogSelectedAddons(
                                        this@OrderSummuryActivity,
                                        addOnList
                                )
                            }
                        }
                        holder.itemView.tvNotes.setOnClickListener {
                            if (!orderHistoryList.get(position).itemNotes.isNullOrEmpty()) {
                                Common.alertNotesDialog(
                                        this@OrderSummuryActivity,
                                        orderHistoryList.get(position).itemNotes
                                )
                            }
                        }
                    }

                    override fun setItemLayout(): Int {
                        return R.layout.row_orderitemsummary
                    }

                }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderSummuryActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }

    fun openDialogPromocode() {
        val dialog: Dialog = Dialog(this@OrderSummuryActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp
        dialog.setContentView(R.layout.dlg_procode)
        dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ivCancel = dialog.findViewById<ImageView>(R.id.ivCancel)
        val rvPromocode = dialog.findViewById<RecyclerView>(R.id.rvPromoCode)
        val tvNoDataFound = dialog.findViewById<TextView>(R.id.tvNoDataFound)
        if (promocodeList!!.size > 0) {
            rvPromocode.visibility = View.VISIBLE
            tvNoDataFound.visibility = View.GONE
            setPromocodeAdaptor(promocodeList!!, rvPromocode, dialog)
        } else {
            rvPromocode.visibility = View.GONE
            tvNoDataFound.visibility = View.VISIBLE
        }

        ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setPromocodeAdaptor(
            promocodeList: ArrayList<PromocodeModel>,
            rvPromocode: RecyclerView,
            dialog: Dialog
    ) {
        val orderHistoryAdapter =
                object : BaseAdaptor<PromocodeModel>(this@OrderSummuryActivity, promocodeList) {
                    override fun onBindData(
                            holder: RecyclerView.ViewHolder?,
                            `val`: PromocodeModel,
                            position: Int
                    ) {
                        val tvTitleOrderNumber: TextView =
                                holder!!.itemView.findViewById(R.id.tvTitleOrderNumber)
                        val tvPromocode: TextView = holder.itemView.findViewById(R.id.tvPromocode)
                        val tvPromocodeDescription: TextView =
                                holder.itemView.findViewById(R.id.tvPromocodeDescription)
                        val tvCopyCode: TextView = holder.itemView.findViewById(R.id.tvCopyCode)

                        tvTitleOrderNumber.text = promocodeList.get(position).getOffer_code()
                        tvPromocode.text = promocodeList.get(position).getOffer_name()
                        tvPromocodeDescription.text = promocodeList.get(position).getDescription()

                        tvCopyCode.setOnClickListener {
                            dialog.dismiss()
                            promocodePrice = promocodeList[position].getOffer_amount()!!.toFloat()
                            edPromocode.text = promocodeList[position].getOffer_code()
                        }
                    }

                    override fun setItemLayout(): Int {
                        return R.layout.row_promocode
                    }

                }
        rvPromocode.adapter = orderHistoryAdapter
        rvPromocode.layoutManager = LinearLayoutManager(this@OrderSummuryActivity)
        rvPromocode.itemAnimator = DefaultItemAnimator()
        rvPromocode.isNestedScrollingEnabled = true
    }

    private fun getLocation() {
        val autocompleteIntent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fieldSelector!!.allFields
        ).build(this@OrderSummuryActivity)
        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                AutocompleteActivity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    edAddress.text = place.getAddress()
                    val latLng: String = place.latLng.toString()
                    val tempArray =
                            latLng.substring(latLng.indexOf("(") + 1, latLng.lastIndexOf(")"))
                                    .split(",")
                                    .toTypedArray()
                    lat = tempArray[0].toDouble()
                    lon = tempArray[1].toDouble()

                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Common.showErrorFullMsg(this@OrderSummuryActivity, resources.getString(R.string.invalid_key))
                }
                AutocompleteActivity.RESULT_CANCELED -> {
                    Common.getLog("Nice", " RESULT_CANCELED : AutoComplete Places")
                }
            }
        } else if (resultCode == 500) {

            llAddress.visibility = View.VISIBLE
            llLandmark.visibility = View.VISIBLE
            llBuilding.visibility = View.VISIBLE
            llZipCode.visibility = View.VISIBLE
            edAddress.visibility = View.VISIBLE
            edAddress.text = data?.getStringExtra("Address")
            edBuilding.text = data?.getStringExtra("FlatNo")
            edLandmark.text = data?.getStringExtra("landMark")
            edPinCode.text = data?.getStringExtra("PinCode")
            deliveryCharge=data?.getStringExtra("DeliveryCharge")?:""
            val addresstype = data?.getIntExtra("addressType", 0)
            val addressTypeArray = arrayOf("Home", "Work", "Other")

            val orderTax: Float = (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!.toFloat()) / 100.toFloat()

            tvOrderDeliveryCharge.text =
               getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                       Locale.US,
                       "%,.2f",
                   deliveryCharge.toDouble()
               )
                val totalprice =
                        summaryModel.getOrder_total()!!.toFloat() + summaryModel.getTax()!!.toFloat() + deliveryCharge.toFloat()
                tvOrderTotalCharge.text = getStringPref(this@OrderSummuryActivity, isCurrancy) + String.format(
                                Locale.US,
                                "%,.2f",
                                totalprice
                        )

            when (addresstype) {
                1 -> {
                    tvType.text = addressTypeArray[0]
                }
                2 -> {
                    tvType.text = addressTypeArray[1]

                }
                3 -> {
                    tvType.text = addressTypeArray[2]
                }
            }
        }
    }

    private fun callApiCheckPinCode(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@OrderSummuryActivity)
        val call = ApiClient.getClient.setCheckPinCode(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                    call: Call<SingleResponse>,
                    response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val singleResponse: SingleResponse = response.body()!!
                    if (singleResponse.getStatus() == "0") {
                        edPinCode.setText("")
                        Common.showErrorFullMsg(
                                this@OrderSummuryActivity,
                                singleResponse.getMessage()!!
                        )
                    } else if (singleResponse.getStatus() == "1") {
                        if (summaryModel.getOrder_total()!!.toDouble() > getStringPref(
                                        this@OrderSummuryActivity,
                                        isMiniMum
                                )!!.toDouble() && summaryModel.getOrder_total()!!
                                        .toDouble() < getStringPref(
                                        this@OrderSummuryActivity,
                                        isMaximum
                                )!!.toDouble()
                        ) {
                            val intent =
                                    Intent(this@OrderSummuryActivity, PaymentPayActivity::class.java)
                            val strTotalCharge = tvOrderTotalCharge.text.toString()
                                    .replace(getStringPref(this@OrderSummuryActivity, isCurrancy)!!, "")
                            val strActuleCharge = strTotalCharge.replace(",", "")
                            val orderTax: Float =
                                    (summaryModel.getOrder_total()!!.toFloat() * summaryModel.getTax()!!
                                            .toFloat()) / 100
                            intent.putExtra(
                                    "getAmount",
                                    String.format(Locale.US, "%.2f", strActuleCharge.toDouble())
                            )
                            intent.putExtra("getAddress", edAddress.text.toString())
                            intent.putExtra("getTax", summaryModel.getTax())
                            intent.putExtra(
                                    "getTaxAmount",
                                    String.format(Locale.US, "%.2f", summaryModel.getTax()!!.toFloat())
                            )
                            intent.putExtra(
                                    "delivery_charge",
                                    String.format(
                                            Locale.US,
                                            "%.2f",
                                            deliveryCharge.toDouble()
                                    )
                            )
                            intent.putExtra("promocode", tvPromoCodeApply.text.toString())
                            intent.putExtra("discount_pr", discountPer)
                            intent.putExtra("discount_amount", discountAmount)
                            intent.putExtra("order_notes", edNotes.text.toString())
                            intent.putExtra("lat", lat.toString())
                            intent.putExtra("lon", lon.toString())
                            intent.putExtra("order_type", "1")
                            intent.putExtra("building", edBuilding.text.toString())
                            intent.putExtra("landmark", edLandmark.text.toString())
                            intent.putExtra("pincode", edPinCode.text.toString())
                            startActivity(intent)
                        } else {
                            alertErrorOrValidationDialog(
                                    this@OrderSummuryActivity,
                                    "Order amount must be between ${
                                    getStringPref(
                                            this@OrderSummuryActivity,
                                            isCurrancy
                                    ) + getStringPref(this@OrderSummuryActivity, isMiniMum)
                                    } and ${
                                    getStringPref(
                                            this@OrderSummuryActivity,
                                            isCurrancy
                                    ) + getStringPref(this@OrderSummuryActivity, isMaximum)
                                    }"
                            )
                        }

                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                            this@OrderSummuryActivity,
                            error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@OrderSummuryActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }


}