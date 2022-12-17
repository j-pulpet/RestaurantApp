package com.foodapp.app.activity


import android.os.Handler
import android.os.Looper
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.printKeyHash
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getBooleanPref


class SplashActivity : BaseActivity(){
    override fun setLayout(): Int {
       return R.layout.activity_splash
    }

    override fun InitView() {
        Common.getLog("getShaKey",printKeyHash(this@SplashActivity)!!)
        Common.getCurrentLanguage(this@SplashActivity, false)
        Handler(Looper.getMainLooper()).postDelayed({
            if(!getBooleanPref(this@SplashActivity,SharePreference.isTutorial)){
                openActivity(TutorialActivity::class.java)
                finish()
            }else{
                openActivity(DashboardActivity::class.java)
                finish()
            }
        },3000)
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@SplashActivity, false)
    }
}