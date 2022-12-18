package com.foodapp.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodapp.app.R
import com.foodapp.app.activity.DashboardActivity
import com.foodapp.app.activity.OrderDetailActivity
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.ListResponse
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.base.BaseFragmnet
import com.foodapp.app.model.OrderHistoryModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.Common.getCurrancy
import com.foodapp.app.utils.Common.getDate
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.userId
import kotlinx.android.synthetic.main.fragment_orderhistory.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class OrderHistoryFragment : BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_orderhistory
    }

    override fun Init(view: View) {
        Common.getCurrentLanguage(activity!!, false)
        if (isCheckNetwork(activity!!)) {
            callApiOrderHistory()
        } else {
            alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
        }

        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }

        swiperefresh.setOnRefreshListener {
            if (isCheckNetwork(activity!!)) {
                swiperefresh.isRefreshing = false
                callApiOrderHistory()
            } else {
                alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
            }
        }
    }

    private fun callApiOrderHistory() {
        showLoadingProgress(activity!!)
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(activity!!, userId)!!
        val call = ApiClient.getClient.getOrderHistory(map)
        call.enqueue(object : Callback<ListResponse<OrderHistoryModel>> {
            override fun onResponse(
                    call: Call<ListResponse<OrderHistoryModel>>,
                    response: Response<ListResponse<OrderHistoryModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<OrderHistoryModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data!!.size > 0) {
                            rvOrderHistory.visibility = View.VISIBLE
                            tvNoDataFound.visibility = View.GONE
                            val foodCategoryList = restResponce.data!!
                            setFoodCategoryAdaptor(foodCategoryList)
                        } else {
                            rvOrderHistory.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                        }

                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        rvOrderHistory.visibility = View.GONE
                        tvNoDataFound.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<OrderHistoryModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        activity!!,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodCategoryAdaptor(orderHistoryList: ArrayList<OrderHistoryModel>) {
        val orderHistoryAdapter =
                object : BaseAdaptor<OrderHistoryModel>(activity!!, orderHistoryList) {
                    @SuppressLint("SetTextI18n", "NewApi", "UseCompatLoadingForDrawables")
                    override fun onBindData(
                            holder: RecyclerView.ViewHolder?,
                            `val`: OrderHistoryModel,
                            position: Int
                    ) {
                        val tvOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvOrderNumber)
                        val tvPrice: TextView = holder.itemView.findViewById(R.id.tvPayment)
                        val tvOrderStatus: TextView = holder.itemView.findViewById(R.id.tvOrderStatus)
                        val tvPaymentType: TextView = holder.itemView.findViewById(R.id.tvPaymentType)
                        val tvOrderDate: TextView = holder.itemView.findViewById(R.id.tvOrderDate)
                        val tvOrderType: TextView = holder.itemView.findViewById(R.id.tvOrderType)
                        val rlOrder: RelativeLayout = holder.itemView.findViewById(R.id.rlOrder)

                        tvOrderNumber.text = orderHistoryList[position].orderNumber.toString()
                        tvPrice.text = getCurrancy(requireActivity()) + String.format(
                                Locale.US,
                                "%,.2f",
                                orderHistoryList[position].totalPrice!!.toDouble()
                        )

                        if (orderHistoryList[position].orderType == "2") {
                            tvOrderType.text = resources.getString(R.string.pickup)
                        } else if (orderHistoryList[position].orderType == "1") {
                            tvOrderType.text = resources.getString(R.string.delivery)
                        }

                        if (orderHistoryList[position].date == null) {
                            tvOrderDate.text = ""
                        } else {
                            tvOrderDate.text = getDate(orderHistoryList[position].date!!)
                        }

                        when {
                            orderHistoryList[position].paymentType!!.toInt() == 0 -> {
                                tvPaymentType.text = resources.getString(R.string.cash)
                            }
                            orderHistoryList[position].paymentType!!.toInt() == 1 -> {
                                tvPaymentType.text = resources.getString(R.string.razorpay)
                            }
                            orderHistoryList[position].paymentType!!.toInt() == 2 -> {
                                tvPaymentType.text = resources.getString(R.string.stripe)
                            }
                            else -> {
                                tvPaymentType.text = resources.getString(R.string.wallet)
                            }
                        }

                        if (orderHistoryList[position].status == "5") {
                            rlOrder.backgroundTintList = ColorStateList.valueOf(
                                    ResourcesCompat.getColor(
                                            resources,
                                            R.color.status1,
                                            null
                                    )
                            )
                            tvOrderStatus.text = resources.getString(R.string.order_cancelled_you)
                        } else if (orderHistoryList[position].status == "6") {
                            rlOrder.backgroundTintList = ColorStateList.valueOf(
                                    ResourcesCompat.getColor(
                                            resources,
                                            R.color.status1,
                                            null
                                    )
                            )
                            tvOrderStatus.text = resources.getString(R.string.order_cancelled_admin)
                        } else {
                            if (orderHistoryList[position].orderType == "1") {
                                when (orderHistoryList[position].status) {
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
                                        tvOrderStatus.text =
                                                resources.getString(R.string.order_delivered)
                                    }
                                }
                            } else {
                                when (orderHistoryList[position].status) {
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
                                                        R.color.status3,
                                                        null
                                                )
                                        )
                                        tvOrderStatus.text =
                                                resources.getString(R.string.order_delivered)
                                    }
                                }
                            }
                        }



                        holder.itemView.setOnClickListener {
                            startActivityForResult(
                                    Intent(
                                            requireActivity(),
                                            OrderDetailActivity::class.java
                                    ).putExtra("order_id", orderHistoryList[position].id.toString())
                                            .putExtra(
                                                    "order_status",
                                                    orderHistoryList[position].status.toString()
                                            ).putExtra("paymentType", orderHistoryList[position].paymentType.toString()).putExtra("orderDate", orderHistoryList[position].date.toString())
                                    , 200)
                        }
                    }

                    override fun setItemLayout(): Int {
                        return R.layout.cell_delivery
                    }

                }
        rvOrderHistory.adapter = orderHistoryAdapter
        rvOrderHistory.layoutManager = LinearLayoutManager(activity!!)
        rvOrderHistory.itemAnimator = DefaultItemAnimator()
        rvOrderHistory.isNestedScrollingEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(activity!!, false)
        if (Common.isCancelledOrder) {
            if (isCheckNetwork(activity!!)) {
                Common.isCancelledOrder = false
                callApiOrderHistory()
            } else {
                alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
            }
        }
    }
}