package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.api.RestOrderDetailResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.OrderDetailModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.api.*
import com.foodapp.app.model.AddonsModel
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.getDate
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.isCurrancy
import kotlinx.android.synthetic.main.activity_orderdetail.*
import kotlinx.android.synthetic.main.activity_orderdetail.ivBack
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderDeliveryCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTaxPrice
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalCharge
import kotlinx.android.synthetic.main.activity_yoursorderdetail.tvOrderTotalPrice
import kotlinx.android.synthetic.main.dlg_confomation.view.*
import kotlinx.android.synthetic.main.row_orderitemsummary.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OrderDetailActivity : BaseActivity() {
    var orderStatus = ""
    var paymentType = ""
    var orderDate = ""
    override fun setLayout(): Int {
        return R.layout.activity_orderdetail
    }

    override fun InitView() {
        orderStatus = intent.getStringExtra("order_status").toString()
        paymentType = intent.getStringExtra("paymentType").toString()
        orderDate = intent.getStringExtra("orderDate").toString()
        if (Common.isCheckNetwork(this@OrderDetailActivity)) {
            callApiOrderDetail()
        } else {
            alertErrorOrValidationDialog(
                this@OrderDetailActivity,
                resources.getString(R.string.no_internet)
            )
        }
        ivBack.setOnClickListener {
            finish()
        }

        if (getStringPref(
                this@OrderDetailActivity,
                SharePreference.SELECTED_LANGUAGE
            ).equals(resources.getString(R.string.language_hindi))
        ) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F

        }

        if (intent.getStringExtra("order_status")?.toInt()!! > 1) {
            tvProceedToPaymnet.visibility = View.GONE
        } else {
            tvProceedToPaymnet.visibility = View.VISIBLE
        }

        tvProceedToPaymnet.setOnClickListener {
            mCancalOrderDialog(intent.getStringExtra("order_id")!!)
        }

    }

    override fun onBackPressed() {
        finish()
    }


    @SuppressLint("NewApi")
    private fun orderInfo(response: RestOrderDetailResponse) {


        when {
            paymentType.toInt() == 0 -> {
                tvPaymentType.text = resources.getString(R.string.cash)
            }
            paymentType.toInt() == 1 -> {
                tvPaymentType.text = resources.getString(R.string.razorpay)
            }
            paymentType.toInt() == 2 -> {
                tvPaymentType.text = resources.getString(R.string.stripe)
            }
            else -> {
                tvPaymentType.text = resources.getString(R.string.wallet)
            }
        }
        if (response.getOrder_type() == "2") {
            tvOrderType.text = resources.getString(R.string.pickup)
        } else if (response.getOrder_type() == "1") {
            tvOrderType.text = resources.getString(R.string.delivery)
        }
        tvOrderDate.text = getDate(orderDate)
        tvOrderNumber?.text = response.getOrder_number()
        if (orderStatus.toString() == "5") {
            rlOrder.backgroundTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.status1,
                    null
                )
            )
            tvOrderStatus.text = resources.getString(R.string.order_cancelled_you)
        } else if (orderStatus.toString() == "6") {
            rlOrder.backgroundTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.status1,
                    null
                )
            )
            tvOrderStatus.text = resources.getString(R.string.order_cancelled_admin)
        } else {
            if (response.getOrder_type() == "1") {
                when (orderStatus.toString()) {
                    "1" -> {
                        rlOrder.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status1,
                                null
                            )
                        )
                        tvOrderStatus.text = resources.getString(R.string.order_place)
                    }
                    "2" -> {
                        rlOrder.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status2,
                                null
                            )
                        )
                        tvOrderStatus.text = resources.getString(R.string.order_ready)
                    }
                    "3" -> {
                        rlOrder.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status3,
                                null
                            )
                        )
                        tvOrderStatus.text = resources.getString(R.string.on_the_way)
                    }
                    "4" -> {
                        rlOrder.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status4,
                                null
                            )
                        )
                        tvOrderStatus.text = resources.getString(R.string.order_delivered)
                    }
                }
            } else {
                when (orderStatus.toString()) {
                    "1" -> {
                        rlOrder.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status1,
                                null
                            )
                        )
                        tvOrderStatus.text = resources.getString(R.string.order_place)
                    }
                    "2" -> {
                        rlOrder.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status2,
                                null
                            )
                        )
                        tvOrderStatus.text = resources.getString(R.string.order_ready)
                    }
                    "4" -> {
                        rlOrder.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.status4,
                                null
                            )
                        )
                        tvOrderStatus.text =
                            resources.getString(R.string.order_pickedUp)
                    }
                }
            }
        }
    }

    private fun callApiOrderDetail() {
        showLoadingProgress(this@OrderDetailActivity)
        val map = HashMap<String, String>()
        map["order_id"] = intent.getStringExtra("order_id")!!
        val call = ApiClient.getClient.setgetOrderDetail(map)
        call.enqueue(object : Callback<RestOrderDetailResponse> {
            override fun onResponse(
                call: Call<RestOrderDetailResponse>,
                response: Response<RestOrderDetailResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: RestOrderDetailResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (restResponce.getData().size > 0) {
                            rvOrderItemFood.visibility = View.VISIBLE
                            setFoodDetailData(restResponce)
                        } else {
                            rvOrderItemFood.visibility = View.GONE
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        rvOrderItemFood.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<RestOrderDetailResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setFoodDetailData(response: RestOrderDetailResponse) {
        if (response.getData().size > 0) {
            setFoodCategoryAdaptor(response.getData())
        }

        orderInfo(response)
        if (response.getOrder_type().equals("2")) {
            cvDeliveryAddress.visibility = View.GONE
            cvDriverInformation.visibility = View.GONE
            if (response.getSummery()!!.getPromocode() == null) {
                rlDiscount.visibility = View.GONE
                if (response.getSummery()!!.getOrder_notes() == null) {
                    tvNotes.text = ""
                    cvOrderNote.visibility = View.GONE
                } else {
                    cvOrderNote.visibility = View.VISIBLE
                    tvNotes.text = response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text = response.getAddress()
                edLandmark.text = response.getLandmark()
                edBuilding.text = response.getBuilding()
                tvOrderTotalPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getOrder_total()!!.toDouble()
                    )
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getTax()!!.toDouble()
                    )
                tvOrderDeliveryCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + "0.00"
                val getTex: Float =
                    (response.getSummery()!!.getOrder_total()!!.toFloat() * response.getSummery()!!
                        .getTax()!!.toFloat()) / 100.toFloat()
//                tvTitleTex.text = "Tax (${response.getSummery()!!.getTax()}%)"
                tvTitleTex.text="Tax"
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!
                            .getTax()!!.toFloat()
                    )
                val totalprice =
                    response.getSummery()!!.getOrder_total()!!.toFloat() + response.getSummery()!!
                        .getTax()!!.toFloat() + 0.00
                tvOrderTotalCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        totalprice
                    )
            } else {
                rlDiscount.visibility = View.VISIBLE
                if (response.getSummery()!!.getOrder_notes() == null) {
                    tvNotes.text = ""
                    cvOrderNote.visibility = View.GONE
                } else {
                    cvOrderNote.visibility = View.VISIBLE
                    tvNotes.text = response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text = response.getAddress()
                edLandmark.text = response.getLandmark()
                edBuilding.text = response.getBuilding()
                tvOrderTotalPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getOrder_total()!!.toDouble()
                    )
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getTax()!!.toDouble()
                    )
                tvOrderDeliveryCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + "0.00"

                val getTex: Float =
                    (response.getSummery()!!.getOrder_total()!!.toFloat() * response.getSummery()!!
                        .getTax()!!.toFloat()) / 100
                tvTitleTex.text = "Tax"
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!
                            .getTax()!!.toFloat()
                    )

                tvDiscountOffer.text = "-" + getStringPref(
                    this@OrderDetailActivity,
                    isCurrancy
                ) + String.format(
                    Locale.US,
                    "%,.02f",
                    response.getSummery()!!.getDiscount_amount()!!.toFloat()
                )
                tvPromoCodeApply.text = response.getSummery()!!.getPromocode()


                val subtotal =
                    response.getSummery()!!.getOrder_total()!!.toFloat() - response.getSummery()!!
                        .getDiscount_amount()!!.toFloat()
                val totalprice = subtotal + response.getSummery()!!
                    .getTax()!!.toFloat() + 0.00
                tvOrderTotalCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        totalprice
                    )
            }
        } else {
            cvDeliveryAddress.visibility = View.VISIBLE
            //cvPinCode.visibility=View.VISIBLE
            edPinCode.text = response.getPincode()
            if (intent.getStringExtra("order_status")!! == "3" || intent.getStringExtra("order_status")!! == "4"
            ) {
                cvDriverInformation.visibility = View.VISIBLE
                llCall.setOnClickListener {
                    val call: Uri = Uri.parse("tel:${response.getSummery()!!.getDriver_mobile()}")
                    val surf = Intent(Intent.ACTION_DIAL, call)
                    startActivity(surf)
                }
                tvUserName.text = response.getSummery()!!.getDriver_name()

                Glide.with(this@OrderDetailActivity)
                    .load(response.getSummery()!!.getDriver_profile_image())
                    .placeholder(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_placeholder,
                            null
                        )
                    ).centerCrop()
                    .into(ivUserDetail)
            } else {
                cvDriverInformation.visibility = View.GONE
            }

            if (response.getSummery()!!.getPromocode() == null) {
                rlDiscount.visibility = View.GONE
                if (response.getSummery()!!.getOrder_notes() == null) {
                    tvNotes.text = ""
                    cvOrderNote.visibility = View.GONE
                } else {
                    cvOrderNote.visibility = View.VISIBLE
                    tvNotes.text = response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text = response.getAddress()
                edLandmark.text = response.getLandmark()
                edBuilding.text = response.getBuilding()
                tvOrderTotalPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getOrder_total()!!.toDouble()
                    )
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getTax()!!.toDouble()
                    )
                tvOrderDeliveryCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getDelivery_charge()!!.toDouble()
                    )

                val getTex: Float =
                    (response.getSummery()!!.getOrder_total()!!.toFloat() * response.getSummery()!!
                        .getTax()!!.toFloat()) / 100.toFloat()
                tvTitleTex.text = "Tax"
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!
                            .getTax()!!.toFloat()
                    )
                val totalprice = response.getSummery()!!.getOrder_total()!!
                    .toFloat() + response.getSummery()!!
                    .getTax()!!.toFloat() + response.getSummery()!!.getDelivery_charge()!!.toFloat()
                tvOrderTotalCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        totalprice
                    )
            } else {
                rlDiscount.visibility = View.VISIBLE
                if (response.getSummery()!!.getOrder_notes() == null) {
                    tvNotes.text = ""
                    cvOrderNote.visibility = View.GONE
                } else {
                    cvOrderNote.visibility = View.VISIBLE
                    tvNotes.text = response.getSummery()!!.getOrder_notes()
                }
                tvOrderAddress.text = response.getAddress()
                edLandmark.text = response.getLandmark()
                edBuilding.text = response.getBuilding()
                tvOrderTotalPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getOrder_total()!!.toDouble()
                    )
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getTax()!!.toDouble()
                    )
                tvOrderDeliveryCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!.getDelivery_charge()!!.toDouble()
                    )

                val getTex: Float =
                    (response.getSummery()!!.getOrder_total()!!.toFloat() * response.getSummery()!!
                        .getTax()!!.toFloat()) / 100
                tvTitleTex.text = "Tax"
                tvOrderTaxPrice.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        response.getSummery()!!
                            .getTax()!!.toFloat()
                    )

                tvDiscountOffer.text = "-" + getStringPref(
                    this@OrderDetailActivity,
                    isCurrancy
                ) + String.format(
                    Locale.US,
                    "%,.02f",
                    response.getSummery()!!.getDiscount_amount()!!.toFloat()
                )
                tvPromoCodeApply.text = response.getSummery()!!.getPromocode()

                val subtotal =
                    response.getSummery()!!.getOrder_total()!!.toFloat() - response.getSummery()!!
                        .getDiscount_amount()!!.toFloat()
                val totalprice =
                    subtotal + response.getSummery()!!
                        .getTax()!!.toFloat() + response.getSummery()!!.getDelivery_charge()!!.toFloat()
                tvOrderTotalCharge.text =
                    getStringPref(this@OrderDetailActivity, isCurrancy) + String.format(
                        Locale.US,
                        "%,.02f",
                        totalprice
                    )
            }
        }

    }

    private fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderDetailModel>) {
        val orderHistoryAdapter =
            object : BaseAdaptor<OrderDetailModel>(this@OrderDetailActivity, orderHistoryList) {
                @SuppressLint("SetTextI18n", "NewApi")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderDetailModel,
                    position: Int
                ) {

                    val ivFoodItem: ImageView = holder!!.itemView.findViewById(R.id.ivFoodCart)
                    val tvOrderFoodName: TextView = holder.itemView.findViewById(R.id.tvFoodName)
                    val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPrice)
                    val tvQtyNumber: TextView = holder.itemView.findViewById(R.id.tvQtyPrice)
                    val tvNotes: TextView = holder.itemView.findViewById(R.id.tvNotes)
                    val tvAddons: TextView = holder.itemView.findViewById(R.id.tvAddons)
                    val tvWeight: TextView = holder.itemView.findViewById(R.id.tvWeight)

                    tvOrderFoodName.text = orderHistoryList[position].getItem_name()
                    tvWeight.text=orderHistoryList[position].getVariation()
                    val price=orderHistoryList[position].getQty()!!.toInt()*orderHistoryList[position].getTotal_price()!!.toDouble()
                    tvPrice.text = getStringPref(
                        this@OrderDetailActivity,
                        isCurrancy
                    ) + String.format(
                        Locale.US,
                        "%,.2f",
                        price
                    )
                    tvQtyNumber.text =
                        resources.getString(R.string.qty) + " ${orderHistoryList[position].getQty()}"

                    Glide.with(this@OrderDetailActivity)
                        .load(orderHistoryList[position].getItemImage())
                        .placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_placeholder,
                                null
                            )
                        ).centerCrop()
                        .into(ivFoodItem)


                    if (orderHistoryList[position].getAddons_id() != "") {
                        tvAddons.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        )
                    } else {
                        tvAddons.visibility = View.GONE
                        tvAddons.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.gray,
                                null
                            )
                        )
                    }
                    if (orderHistoryList[position].getItem_notes() == null || orderHistoryList[position].getItem_notes() == "") {
                        tvNotes.visibility = View.GONE

                        tvNotes.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.gray,
                                null
                            )
                        )
                    } else {
                        tvNotes.backgroundTintList = ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.colorPrimary,
                                null
                            )
                        )
                    }

                    val getIds =
                        orderHistoryList[position].getAddons_id()?.split(",")?.toList()
                    val getNames =
                        orderHistoryList[position].getAddOnsName()?.split(",")?.toTypedArray()
                    val getPrice =
                        orderHistoryList[position].getAddOnsPrice()?.split(",")?.toTypedArray()

                    val addOnList = ArrayList<AddonsModel>()
                    for (i in getIds?.indices!!) {
                        if (getIds[i] != "" || getPrice?.get(i).toString() != "") {
                            addOnList.add(
                                AddonsModel(
                                    getPrice?.get(i).toString(), getNames?.get(i).toString(),
                                    getIds[i]
                                )
                            )
                        } else {
                            return
                        }
                    }

                    holder.itemView.tvAddons.setOnClickListener {
                        if (addOnList.size > 0) {
                            Common.openDialogSelectedAddons(this@OrderDetailActivity, addOnList)
                        }
                    }

                    holder.itemView.tvNotes.setOnClickListener {
                        if (!orderHistoryList[position].getItem_notes().isNullOrEmpty()) {
                            Common.alertNotesDialog(
                                this@OrderDetailActivity,
                                orderHistoryList[position].getItem_notes()
                            )
                        }
                    }

                }

                override fun setItemLayout(): Int {
                    return R.layout.row_orderitemsummary
                }
            }
        rvOrderItemFood.adapter = orderHistoryAdapter
        rvOrderItemFood.layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        rvOrderItemFood.itemAnimator = DefaultItemAnimator()
        rvOrderItemFood.isNestedScrollingEnabled = true
    }

    @SuppressLint("InflateParams")
    fun mCancalOrderDialog(strOrderId: String) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(this@OrderDetailActivity, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(this@OrderDetailActivity)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            mView.tvDesc.text = resources.getString(R.string.wallet_description)
            val finalDialog: Dialog = dialog
            mView.tvYes.setOnClickListener {
                finalDialog.dismiss()
                val map = HashMap<String, String>()
                map["order_id"] = strOrderId
                if (Common.isCheckNetwork(this@OrderDetailActivity)) {
                    callApiOrder(map)
                } else {
                    alertErrorOrValidationDialog(
                        this@OrderDetailActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            mView.tvNo.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callApiOrder(map: java.util.HashMap<String, String>) {
        showLoadingProgress(this@OrderDetailActivity)
        val call = ApiClient.getClient.setOrderCancel(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        Common.isCancelledOrder = true
                        finish()
                    } else if (restResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@OrderDetailActivity,
                            restResponse.getMessage()
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@OrderDetailActivity,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@OrderDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}