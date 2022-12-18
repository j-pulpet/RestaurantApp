package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.userRefralAmount
import com.foodapp.app.utils.SharePreference.Companion.userRefralCode
import kotlinx.android.synthetic.main.activity_referandearn.*
import kotlinx.android.synthetic.main.activity_referandearn.ivBack
import java.util.*


class RefarAndEarnActivity : BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_referandearn

    @SuppressLint("SetTextI18n")
    override fun InitView() {
        val price = getStringPref(this@RefarAndEarnActivity, userRefralAmount)
        val formattedPrice: String = getStringPref(this@RefarAndEarnActivity, SharePreference.isCurrancy) + String.format(Locale.US, "%,.2f", price?.toDouble())

        Common.getCurrentLanguage(this@RefarAndEarnActivity, false)
        tvRefareAndEarn.text = "Share this code with a friend and you both \n could be eligible for $formattedPrice bonus amount \n under our Referral Program."
        ivBack.setOnClickListener {
            finish()
        }
        if (getStringPref(this@RefarAndEarnActivity, SharePreference.SELECTED_LANGUAGE)
                        .equals(resources.getString(R.string.language_hindi))) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }
        edPromocode.text = getStringPref(
                this@RefarAndEarnActivity,
                userRefralCode
        )
        tvBtnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Use this code ${getStringPref(this@RefarAndEarnActivity, userRefralCode)} to register with ${resources.getString(R.string.app_name)} & get bonus amount $formattedPrice")
            startActivity(Intent.createChooser(intent, "choose one"))
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@RefarAndEarnActivity, false)
    }
}