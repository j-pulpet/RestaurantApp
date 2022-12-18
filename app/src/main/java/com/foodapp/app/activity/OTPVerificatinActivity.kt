package com.foodapp.app.activity

import android.os.CountDownTimer
import android.view.View
import com.google.gson.JsonObject
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_otpverification.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class OTPVerificatinActivity:BaseActivity() {
    var strEmail: String = ""
    var strToken=""
    var loginUserType=""
    override fun setLayout(): Int {
        return R.layout.activity_otpverification
    }

    override fun InitView() {
        loginUserType=SharePreference.getStringPref(this@OTPVerificatinActivity,SharePreference.UserLoginType)?:""
        strEmail =intent.getStringExtra("email")!!
        strToken= FirebaseInstanceId.getInstance().token.toString()
        tvCheckout.setOnClickListener {
            Common.closeKeyBoard(this@OTPVerificatinActivity)
            if (edOTP.text.toString().length != 6) {
                Common.alertErrorOrValidationDialog(this@OTPVerificatinActivity,resources.getString(R.string.validation_otp))
            } else {
                if(loginUserType=="1")
                {
                    val map = HashMap<String, String>()
                    map["mobile"] = strEmail
                    map["otp"] = edOTP.text.toString()
                    map["token"] = strToken
                    if (Common.isCheckNetwork(this@OTPVerificatinActivity)) {
                        callApiOTP(map)
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@OTPVerificatinActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }else
                {
                    val map = HashMap<String, String>()
                    map["email"] = strEmail
                    map["otp"] = edOTP.text.toString()
                    map["token"] = strToken
                    if (Common.isCheckNetwork(this@OTPVerificatinActivity)) {
                        callApiOTP(map)
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@OTPVerificatinActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }



            }
        }

        object : CountDownTimer(120000, 1000) {
            override fun onTick(millis: Long) {
                llOTP.visibility= View.GONE
                tvResendOtp.visibility = View.GONE
                tvTimer.visibility= View.VISIBLE
                val timer=String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millis)))
                tvTimer.text = timer
                if(timer=="00:00"){
                    tvTimer.visibility= View.GONE
                    llOTP.visibility= View.VISIBLE
                    tvResendOtp.visibility = View.VISIBLE
                }
            }
            override fun onFinish() {
                tvTimer.visibility= View.GONE
                llOTP.visibility= View.VISIBLE
                tvResendOtp.visibility = View.VISIBLE
            }
        }.start()

        tvResendOtp.setOnClickListener {
            Common.closeKeyBoard(this@OTPVerificatinActivity)
            val map = HashMap<String, String>()
            if(loginUserType=="1"){
                map["mobile"] = strEmail

            }else
            {
                map["email"] = strEmail

            }
            if (Common.isCheckNetwork(this@OTPVerificatinActivity)) {
                callApiResendOTP(map)
            } else {
                Common.alertErrorOrValidationDialog(
                        this@OTPVerificatinActivity,
                        resources.getString(R.string.no_internet)
                )
            }
        }
    }

    private fun callApiOTP(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@OTPVerificatinActivity)
        val call: Call<JsonObject> = ApiClient.getClient.setEmailVerify(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val mainObject = JSONObject(response.body().toString())
                    val statusType = mainObject.getInt("status")
                    val statusMessage = mainObject.getString("message")
                    if (statusType == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@OTPVerificatinActivity, statusMessage)
                    } else if (statusType == 1) {
                        Common.dismissLoadingProgress()
                        SharePreference.setStringPref(
                                this@OTPVerificatinActivity,
                                SharePreference.userId,
                                mainObject.getJSONObject("data").getString("id")
                        )
                        SharePreference.setStringPref(
                                this@OTPVerificatinActivity,
                                SharePreference.userName,
                                mainObject.getJSONObject("data").getString("name")
                        )
                        SharePreference.setStringPref(
                                this@OTPVerificatinActivity,
                                SharePreference.userEmail,
                                mainObject.getJSONObject("data").getString("email")
                        )
                        SharePreference.setStringPref(
                                this@OTPVerificatinActivity,
                                SharePreference.loginType,
                                mainObject.getJSONObject("data").getString("login_type")
                        )
                        SharePreference.setStringPref(
                                this@OTPVerificatinActivity,
                                SharePreference.userMobile,
                                mainObject.getJSONObject("data").getString("mobile")
                        )
                        SharePreference.setStringPref(
                                this@OTPVerificatinActivity,
                                SharePreference.userProfile,
                                mainObject.getJSONObject("data").getString("profile_image")
                        )
                        SharePreference.setStringPref(
                                this@OTPVerificatinActivity,
                                SharePreference.userRefralCode,
                                mainObject.getJSONObject("data").getString("referral_code")
                        )
                        SharePreference.setBooleanPref(
                                this@OTPVerificatinActivity,
                                SharePreference.isLogin,
                                true
                        )
                        openActivity(DashboardActivity::class.java)
                        finish()
                    }
                } else {
                    if(response.code()==500){
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@OTPVerificatinActivity, resources.getString(R.string.error_msg))
                    }else{
                        val mainErrorObject = JSONObject(response.errorBody()!!.string())
                        val strMessage = mainErrorObject.getString("message")
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@OTPVerificatinActivity, strMessage)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@OTPVerificatinActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }
    private fun callApiResendOTP(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@OTPVerificatinActivity)
        val call: Call<SingleResponse> = ApiClient.getClient.setResendEmailVerification(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                    call: Call<SingleResponse>,
                    response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: SingleResponse = response.body()!!
                    if (registrationResponse.getStatus().equals("1")) {
                        edOTP.text.clear()
                        Common.dismissLoadingProgress()
                        Common.showSuccessFullMsg(this@OTPVerificatinActivity,registrationResponse.getMessage()!!)
                    } else if (registrationResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(this@OTPVerificatinActivity, registrationResponse.getMessage()!!)
                    }
                }else  {
                    val error= JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(
                            this@OTPVerificatinActivity,
                            error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                        this@OTPVerificatinActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onBackPressed() {
        Common.closeKeyBoard(this@OTPVerificatinActivity)
        openActivity(LoginActivity::class.java)
        finish()
        finishAffinity()
    }

}