package com.foodapp.app.activity

import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_successpayment.*

class PaymentsuccessfulActivity : BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_successpayment

    override fun InitView() {
        tvProceed.setOnClickListener {
            openActivity(DashboardActivity::class.java)
            finishAffinity()
        }
    }

    override fun onBackPressed() {
        return
    }
}