package com.foodapp.app.activity

import android.view.View
import android.webkit.WebViewClient
import androidx.core.content.res.ResourcesCompat
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.activity_privacy_policy.ivBack

class ProvacyPolicyActivity:BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_privacy_policy

    override fun InitView() {
        ivBack.setOnClickListener {
            finish()
        }

        webView.setWebViewClient(WebViewClient())
        webView.getSettings().setLoadsImagesAutomatically(true)
        webView.getSettings().setJavaScriptEnabled(true)
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        if(intent.getBooleanExtra("Policy",false)){
            webView.loadUrl(ApiClient.PrivicyPolicy)
            tvTitle.text=resources.getString(R.string.privacy_policy)
        }else{
            webView.loadUrl(ApiClient.aboutus)
            tvTitle.text=resources.getString(R.string.about_us)
        }


        if(SharePreference.getStringPref(this@ProvacyPolicyActivity, SharePreference.SELECTED_LANGUAGE)
                .equals(resources.getString(R.string.language_hindi))){
            ivBack.rotation= 180F
        }else{
            ivBack.rotation= 0F
        }
    }
}