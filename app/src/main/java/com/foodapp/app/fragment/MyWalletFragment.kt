package com.foodapp.app.fragment

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodapp.app.base.BaseFragmnet
import com.foodapp.app.R
import com.foodapp.app.activity.DashboardActivity
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.ListResponse
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.WalletModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_mywallet.*
import kotlinx.android.synthetic.main.fragment_mywallet.ivMenu
import kotlinx.android.synthetic.main.fragment_mywallet.swiperefresh
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MyWalletFragment:BaseFragmnet() {
    var transactionHistoryList=ArrayList<WalletModel>()
    var transactionAdaptor:BaseAdaptor<WalletModel>?=null

    override fun setView(): Int = R.layout.fragment_mywallet

    override fun Init(view: View) {
        Common.getCurrentLanguage(activity!!, false)
        transactionHistoryList= ArrayList()
        setFoodCategoryAdaptor(transactionHistoryList)


        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }

        if (Common.isCheckNetwork(activity!!)) {
            callApiOrderHistory()
        } else {
            Common.alertErrorOrValidationDialog(
                activity!!,
                    resources.getString(R.string.no_internet)
            )
        }




        swiperefresh.setOnRefreshListener { // Your code to refresh the list here.
            if (Common.isCheckNetwork(activity!!)) {
                swiperefresh.isRefreshing=false
                transactionHistoryList.clear()
                callApiOrderHistory()
            } else {
                Common.alertErrorOrValidationDialog(
                    activity!!,
                        resources.getString(R.string.no_internet)
                )
            }
        }
    }


    private fun callApiOrderHistory() {
        Common.showLoadingProgress(activity!!)
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(activity!!, SharePreference.userId)!!
        val call = ApiClient.getClient.getWallet(map)
        call.enqueue(object : Callback<ListResponse<WalletModel>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ListResponse<WalletModel>>,
                response: Response<ListResponse<WalletModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<WalletModel> = response.body()!!
                    if (restResponce.status==1) {
                        Common.dismissLoadingProgress()
                        tvNoDataFound.visibility= View.GONE
                        rvTransactionHistory.visibility= View.VISIBLE
                        val orderHistoryResponse: ArrayList<WalletModel> = restResponce.data!!
                        if(orderHistoryResponse.isNullOrEmpty()){
                            tvNoDataFound.visibility= View.VISIBLE
                            rvTransactionHistory.visibility= View.GONE
                        }else{
                            if(orderHistoryResponse.size>0){
                                tvNoDataFound.visibility= View.GONE
                                rvTransactionHistory.visibility= View.VISIBLE
                                transactionHistoryList.addAll(orderHistoryResponse)
                                transactionAdaptor!!.notifyDataSetChanged()
                            }else{
                                tvNoDataFound.visibility= View.VISIBLE
                                rvTransactionHistory.visibility= View.GONE
                            }
                        }
                        if(restResponce.walletamount==""||restResponce.walletamount==null||restResponce.walletamount=="0.00"){
                            tvPriceWallet.text=Common.getCurrancy(activity!!)+"00.00"
                        }else{
                            tvPriceWallet.text=Common.getCurrancy(activity!!)+String.format(Locale.US,"%,.2f",restResponce.walletamount.toDouble())
                        }
                    } else if (restResponce.status==0) {
                        tvNoDataFound.visibility= View.GONE
                        rvTransactionHistory.visibility= View.VISIBLE
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(activity!!, restResponce.message)
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<WalletModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodCategoryAdaptor(walletHistoryList: ArrayList<WalletModel>) {
        transactionAdaptor= object : BaseAdaptor<WalletModel>(activity!!, walletHistoryList) {
            @SuppressLint("SetTextI18n", "NewApi", "UseCompatLoadingForDrawables")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: WalletModel,
                position: Int
            ) {
                val tvOrderNumber: TextView = holder!!.itemView.findViewById(R.id.tvOrderNumber)
                val tvPrice: TextView = holder.itemView.findViewById(R.id.tvOrderPrice)
                val tvOrderStatus: TextView = holder.itemView.findViewById(R.id.tvOrderStatus)
                val tvOrderDate: TextView = holder.itemView.findViewById(R.id.tvOrderDate)
                val ivTrading: ImageView = holder.itemView.findViewById(R.id.ivTrading)

                if(walletHistoryList.get(position).transactionType=="2"||walletHistoryList.get(position).transactionType=="1"){
                    tvOrderNumber.text = "Order No : "+walletHistoryList.get(position).orderNumber.toString()
                    if(walletHistoryList.get(position).transactionType=="1"){
                        tvOrderStatus.text="Order Cancelled"
                        tvOrderStatus.setTextColor(ResourcesCompat.getColor(resources,R.color.red,null))
                        tvPrice.text ="-" +Common.getCurrancy(activity!!) +String.format(Locale.US,"%,.2f",walletHistoryList[position].wallet!!.toDouble())
                        tvPrice.setTextColor(ResourcesCompat.getColor(resources,R.color.red,null))
                        ivTrading.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_trred,null))
                    }else if(walletHistoryList.get(position).transactionType=="2"){
                        tvOrderStatus.text="Order Confirmed"
                        tvOrderStatus.setTextColor(ResourcesCompat.getColor(resources,R.color.light_green,null))
                        tvPrice.text =Common.getCurrancy(activity!!) +String.format(Locale.US,"%,.2f",walletHistoryList[position].wallet!!.toDouble())
                        tvPrice.setTextColor(ResourcesCompat.getColor(resources,R.color.light_green,null))
                        ivTrading.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_trgreen,null))
                    }
                }else{
                    if(walletHistoryList.get(position).username!=null){
                        tvOrderNumber.text = walletHistoryList.get(position).username.toString()
                    }else{
                        tvOrderNumber.text = ""
                    }
                    ivTrading.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_trgreen,null))
                    tvOrderStatus.text="Referral Earning"
                    tvOrderStatus.setTextColor(ResourcesCompat.getColor(resources,R.color.light_green,null))
                    tvPrice.text =Common.getCurrancy(activity!!) +String.format(Locale.US,"%,.2f",walletHistoryList[position].wallet!!.toDouble())
                    tvPrice.setTextColor(ResourcesCompat.getColor(resources,R.color.light_green,null))
                }

                if(walletHistoryList[position].date==null){
                    tvOrderDate.text=""
                }else{
                    tvOrderDate.text= Common.getDate(walletHistoryList[position].date!!)
                }

            }

            override fun setItemLayout(): Int {
                return R.layout.row_transactionhistory
            }


        }

        rvTransactionHistory.apply {
            adapter = transactionAdaptor
            layoutManager = LinearLayoutManager(activity!!)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }
    }



    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(activity!!, false)
    }
}