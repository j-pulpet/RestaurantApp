package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.PaymentListResponce
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.PaymentItemModel
import com.foodapp.app.utils.*
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.Common.showErrorFullMsg
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.userEmail
import com.foodapp.app.utils.SharePreference.Companion.userId
import com.foodapp.app.utils.SharePreference.Companion.userMobile
import com.foodapp.app.utils.SharePreference.Companion.userName
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import com.stripe.android.view.CardMultilineWidget
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_payment.ivBack
import kotlinx.android.synthetic.main.activity_yoursorderdetail.*
import kotlinx.android.synthetic.main.row_payment.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class PaymentPayActivity:BaseActivity(),PaymentResultListener,CallBackSuccess {
    var callBackSuccess:CallBackSuccess?=null
    var newStripeDataController:NewStripeDataController?=null
    var strGetData=""
    var strRezorPayKey=""
    var listData=ArrayList<PaymentItemModel>()
    override fun setLayout(): Int {
       return R.layout.activity_payment
    }

    override fun InitView() {
        Common.getCurrentLanguage(this@PaymentPayActivity, false)
        callBackSuccess=this@PaymentPayActivity
        Checkout.preload(this@PaymentPayActivity)

        ivBack.setOnClickListener {
            finish()
        }

        if(getStringPref(this@PaymentPayActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))){
            ivBack.rotation= 180F
        }else{
            ivBack.rotation= 0F
        }

        if(isCheckNetwork(this@PaymentPayActivity)){
            callApiPayment()
        }else{
            alertErrorOrValidationDialog(this@PaymentPayActivity, resources.getString(R.string.no_internet))
        }
        tvPaynow.setOnClickListener {
            if(strGetData == "Wallet"){
                if(listData[0].isSelect){
                    if(listData[0].wallet_amount!!.toDouble()>=intent.getStringExtra("getAmount")!!.toDouble()){
                        if (isCheckNetwork(this@PaymentPayActivity)) {
                            showLoadingProgress(this@PaymentPayActivity)
                            val hasmap = HashMap<String, String>()
                            hasmap["user_id"] = getStringPref(this@PaymentPayActivity, userId)!!
                            hasmap["order_total"] = intent.getStringExtra("getAmount")!!
                            hasmap["razorpay_payment_id"] = ""
                            hasmap["payment_type"] = "3"
                            hasmap["address"] = intent.getStringExtra("getAddress")!!
                            hasmap["promocode"] = intent.getStringExtra("promocode")!!
                            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
                            hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
                            hasmap["tax"] = intent.getStringExtra("getTax")!!
                            hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
                            hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
                            hasmap["lat"] = intent.getStringExtra("lat")!!
                            hasmap["lang"] = intent.getStringExtra("lon")!!
                            hasmap["order_type"] = intent.getStringExtra("order_type")!!
                            hasmap["order_notes"] = intent.getStringExtra("order_notes")!!
                            hasmap["building"] = intent.getStringExtra("building")!!
                            hasmap["landmark"] = intent.getStringExtra("landmark")!!
                            hasmap["pincode"] = intent.getStringExtra("pincode")!!
                            callApiOrder(hasmap)
                        } else {
                            alertErrorOrValidationDialog(
                                    this@PaymentPayActivity,
                                    resources.getString(R.string.no_internet)
                            )
                        }
                    } else{
                        showErrorFullMsg(
                                this@PaymentPayActivity,
                                "You don't have sufficient amount in your wallet to place this order. Please try with another payment option."
                        )
                    }
                }

            }else if(strGetData == "COD"){
                if (isCheckNetwork(this@PaymentPayActivity)) {
                    showLoadingProgress(this@PaymentPayActivity)
                    val hasmap = HashMap<String, String>()
                    hasmap["user_id"] = getStringPref(this@PaymentPayActivity, userId)!!
                    hasmap["order_total"] = intent.getStringExtra("getAmount")!!
                    hasmap["razorpay_payment_id"] = ""
                    hasmap["payment_type"] = "0"
                    hasmap["address"] = intent.getStringExtra("getAddress")!!
                    hasmap["promocode"] = intent.getStringExtra("promocode")!!
                    hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
                    hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
                    hasmap["tax"] = intent.getStringExtra("getTax")!!
                    hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
                    hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
                    hasmap["lat"] = intent.getStringExtra("lat")!!
                    hasmap["lang"] = intent.getStringExtra("lon")!!
                    hasmap["order_type"] = intent.getStringExtra("order_type")!!
                    hasmap["order_notes"] = intent.getStringExtra("order_notes")!!
                    hasmap["building"] = intent.getStringExtra("building")!!
                    hasmap["landmark"] = intent.getStringExtra("landmark")!!
                    hasmap["pincode"] = intent.getStringExtra("pincode")!!
                    callApiOrder(hasmap)
                } else  {
                    alertErrorOrValidationDialog(this@PaymentPayActivity,resources.getString(R.string.no_internet))
                }
            }else if(strGetData == "RazorPay"){
                showLoadingProgress(this@PaymentPayActivity)
                startPayment()
            }else if(strGetData == "Stripe"){
                successfulStripeDialog(this@PaymentPayActivity)
            }else if(strGetData == ""){
                showErrorFullMsg(this@PaymentPayActivity, "Please select any payment option")
            }
        }



    }

    private fun callApiPayment() {
        showLoadingProgress(this@PaymentPayActivity)
        val map = HashMap<String, String>()
        map.put("user_id", getStringPref(this@PaymentPayActivity, userId)!!)
        val call = ApiClient.getClient.getPaymentType(map)
        call.enqueue(object : Callback<PaymentListResponce> {
            override fun onResponse(
                    call: Call<PaymentListResponce>,
                    response: Response<PaymentListResponce>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: PaymentListResponce = response.body()!!
                    if (restResponce.status == 1) {
                        val listPaymentItemModel=PaymentItemModel()
                        listPaymentItemModel.isSelect=false
                        listPaymentItemModel.environment=0
                        listPaymentItemModel.livePublicKey=""
                        listPaymentItemModel.testPublicKey=""
                        listPaymentItemModel.paymentName="Wallet"
                        listPaymentItemModel.wallet_amount=restResponce.walletamount
                        listData.add(listPaymentItemModel)
                        if (restResponce.data!!.size > 0) {
                            listData.addAll(restResponce.data)
                        }
                        setPaymentItemAdaptor(listData)
                    }else{
                        showErrorFullMsg(
                                this@PaymentPayActivity,
                                restResponce.message!!
                        )
                        val listPaymentItemModel=PaymentItemModel()
                        listPaymentItemModel.isSelect=false
                        listPaymentItemModel.environment=0
                        listPaymentItemModel.livePublicKey=""
                        listPaymentItemModel.testPublicKey=""
                        listPaymentItemModel.paymentName="Wallet"
                        listPaymentItemModel.wallet_amount=restResponce.walletamount
                        listData.add(listPaymentItemModel)
                        setPaymentItemAdaptor(listData)
                    }
                } else {
                    dismissLoadingProgress()


                }
            }

            override fun onFailure(call: Call<PaymentListResponce>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@PaymentPayActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }





    override fun onBackPressed() {
        finish()
    }



    private fun startPayment() {
        Common.getLog("test",intent.getStringExtra("getAmount").toString())
        val activity:Activity = this
        val co = Checkout()
        try {
            co.setKeyID(strRezorPayKey)
            val amount=intent.getStringExtra("getAmount")!!.toDouble()*100
            Common.getLog("test",amount.toString())
            val options = JSONObject()
            options.put("name", getStringPref(this@PaymentPayActivity, userName))
            options.put("description", "Restaurant User Payment")
            options.put(
                "image",
                getStringPref(this@PaymentPayActivity, SharePreference.userProfile)
            )
            options.put("currency", "INR")
            options.put("amount", String.format(Locale.US,"%d",amount.toLong()).toString())
            val prefill = JSONObject()
            prefill.put("email", getStringPref(this@PaymentPayActivity, userEmail))
            prefill.put("contact", getStringPref(this@PaymentPayActivity, userMobile))
            options.put("prefill", prefill)
            val theme = JSONObject()
            theme.put("color", "#FD3B2F")
            options.put("theme", theme)
            co.open(activity, options)
        }catch (e: Exception){
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    fun startStripPayment(card: Card){
        if (!card.validateCard()) {
            Toast.makeText(applicationContext, "Invalid card", Toast.LENGTH_SHORT).show()
        } else {
            newStripeDataController!!.CreateToken(card, this@PaymentPayActivity)
        }
     }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
            dismissLoadingProgress()
        }catch (e: Exception){
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
           val hasmap = HashMap<String, String>()
           hasmap["user_id"] = getStringPref(this@PaymentPayActivity, userId)!!
           hasmap["order_total"] = intent.getStringExtra("getAmount")!!
           hasmap["razorpay_payment_id"] = razorpayPaymentId!!
           hasmap["payment_type"] = "1"
           hasmap["address"] = intent.getStringExtra("getAddress")!!
           hasmap["promocode"] = intent.getStringExtra("promocode")!!
           hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
           hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
           hasmap["tax"] = intent.getStringExtra("getTax")!!
           hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
           hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
           hasmap["lat"] = intent.getStringExtra("lat")!!
           hasmap["lang"] = intent.getStringExtra("lon")!!
           hasmap["order_type"] = intent.getStringExtra("order_type")!!
           hasmap["order_notes"] = intent.getStringExtra("order_notes")!!
           hasmap["building"] = intent.getStringExtra("building")!!
           hasmap["landmark"] = intent.getStringExtra("landmark")!!
           hasmap["pincode"] = intent.getStringExtra("pincode")!!
           callApiOrder(hasmap)
        }catch (e: Exception){
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    fun callApiOrder(map: HashMap<String, String>){
        val call = ApiClient.getClient.setOrderPayment(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        openActivity(
                            PaymentsuccessfulActivity::class.java
                        )
                    } else if (restResponse.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@PaymentPayActivity,
                            restResponse.getMessage()
                        )
                    }
                }else{

                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@PaymentPayActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun successfulStripeDialog(act: Activity) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val m_inflater = LayoutInflater.from(act)
            val m_view = m_inflater.inflate(R.layout.dlg_stripe_view, null, false)
            val cvStripe: CardMultilineWidget = m_view.findViewById(R.id.cvStripe)
            val tvOk: TextView = m_view.findViewById(R.id.tvOk)
            val ivCancle: ImageView = m_view.findViewById(R.id.ivCancle)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                if(cvStripe.card!=null){
                    startStripPayment(cvStripe.card!!)
                }
            }
            ivCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@PaymentPayActivity, false)
    }

    override fun onstart() {
       showLoadingProgress(this@PaymentPayActivity)
    }

    override fun success(token: Token?) {
        try{
            //  hasmap["order_from "] = "android"
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] = getStringPref(this@PaymentPayActivity, userId)!!
            hasmap["order_total"]=intent.getStringExtra("getAmount")!!
            hasmap["stripeToken"]=token!!.id
            hasmap["stripeEmail"]=getStringPref(this@PaymentPayActivity, userEmail)!!
            hasmap["payment_type"]="2"
            hasmap["address"] = intent.getStringExtra("getAddress")!!
            hasmap["promocode"] = intent.getStringExtra("promocode")!!
            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
            hasmap["discount_pr"] = intent.getStringExtra("discount_pr")!!
            hasmap["tax"] = intent.getStringExtra("getTax")!!
            hasmap["tax_amount"] = intent.getStringExtra("getTaxAmount")!!
            hasmap["delivery_charge"] = intent.getStringExtra("delivery_charge")!!
            hasmap["lat"] = intent.getStringExtra("lat")!!
            hasmap["lang"] = intent.getStringExtra("lon")!!
            hasmap["order_type"] = intent.getStringExtra("order_type")!!
            hasmap["order_notes"] = intent.getStringExtra("order_notes")!!
            hasmap["building"] = intent.getStringExtra("building")!!
            hasmap["landmark"] = intent.getStringExtra("landmark")!!
            hasmap["pincode"] = intent.getStringExtra("pincode")!!
            callApiOrder(hasmap)
        }catch (e: Exception){
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    override fun failer(error: Exception) {
        dismissLoadingProgress()
        Common.getToast(this@PaymentPayActivity, error.message.toString())
        Log.e("Exception", "Exception in onPaymentSuccess" + error.message)
    }

    fun setPaymentItemAdaptor(paymentItemList: ArrayList<PaymentItemModel>) {
        val orderHistoryAdapter = object : BaseAdaptor<PaymentItemModel>(
                this@PaymentPayActivity,
                paymentItemList
        ) {
            @SuppressLint("SetTextI18n")
            override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: PaymentItemModel,
                    position: Int
            ) {
                if(paymentItemList[position].isSelect){
                    holder!!.itemView.llPaymentView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                }else{
                    holder!!.itemView.llPaymentView.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
                }
                if(paymentItemList[position].paymentName=="Wallet"){
                    holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_wallet, null)!!)
                    holder.itemView.tvPaymentName.text=paymentItemList[position].paymentName+" ("+Common.getCurrancy(this@PaymentPayActivity)+paymentItemList[position].wallet_amount+")"
                } else if(paymentItemList[position].paymentName=="COD"){
                    holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_cod, null)!!)
                    holder.itemView.tvPaymentName.text="Cash Payment"
                }else if(paymentItemList[position].paymentName=="RazorPay"){
                    if(paymentItemList[position].environment==1){
                        strRezorPayKey=paymentItemList[position].testPublicKey!!
                    }else{
                        strRezorPayKey=paymentItemList[position].livePublicKey!!
                    }
                    holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_online, null)!!)
                    holder.itemView.tvPaymentName.text="Razorpay Payment"
                }else if(paymentItemList[position].paymentName=="Stripe"){
                    if(paymentItemList[position].environment==1){
                        newStripeDataController=NewStripeDataController(this@PaymentPayActivity,paymentItemList[position].testPublicKey)
                    }else{
                        newStripeDataController=NewStripeDataController(this@PaymentPayActivity,paymentItemList[position].livePublicKey)
                    }

                    holder.itemView.ivPayment.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_stripe, null)!!)
                    holder.itemView.tvPaymentName.text="Stripe Payment"
                }


                holder.itemView.setOnClickListener {
                    for(i in 0 until paymentItemList.size){
                        paymentItemList[i].isSelect=false
                    }
                    paymentItemList[position].isSelect=true
                    strGetData=paymentItemList[position].paymentName!!
                    notifyDataSetChanged()
                }

            }
            override fun setItemLayout(): Int {
                return R.layout.row_payment
            }
        }
        rvPayment.apply {
            adapter = orderHistoryAdapter
            layoutManager = LinearLayoutManager(this@PaymentPayActivity)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }

    }
}