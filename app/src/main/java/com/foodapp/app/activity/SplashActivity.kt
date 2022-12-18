package com.foodapp.app.activity


import android.os.Handler
import android.os.Looper
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.model.GetLoginTypeResponseModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.printKeyHash
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getBooleanPref
import com.grocery.app.api.ListResopone
import com.grocery.app.model.PinCodeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_splash
    }

    override fun InitView() {
        if (Common.isCheckNetwork(this@SplashActivity)) {
            callGetLoginType()
        } else {
            Common.alertErrorOrValidationDialog(
                this@SplashActivity,
                resources.getString(R.string.no_internet)
            )
        }
        Common.getLog("getShaKey", printKeyHash(this@SplashActivity)!!)
        Common.getCurrentLanguage(this@SplashActivity, false)

//        SharePreference.setStringPref(this@SplashActivity, SharePreference.UserLoginType,"0")
        Handler(Looper.getMainLooper()).postDelayed({
            if (!getBooleanPref(this@SplashActivity, SharePreference.isTutorial)) {
                openActivity(TutorialActivity::class.java)
                finish()
            } else {
                openActivity(DashboardActivity::class.java)
                finish()
            }
        }, 3000)
    }

    private fun callGetLoginType() {
        val call = ApiClient.getClient.getLoginType()
        call.enqueue(object : Callback<GetLoginTypeResponseModel> {
            override fun onResponse(
                call: Call<GetLoginTypeResponseModel>,
                response: Response<GetLoginTypeResponseModel>
            ) {
                if(response.body()?.data=="email")
                {
                    SharePreference.setStringPref(this@SplashActivity, SharePreference.UserLoginType,"0")
                }else if(response.body()?.data=="mobile")
                {
                    SharePreference.setStringPref(this@SplashActivity, SharePreference.UserLoginType,"1")

                }
            }

            override fun onFailure(call: Call<GetLoginTypeResponseModel>, t: Throwable) {
            }


        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@SplashActivity, false)
    }
}