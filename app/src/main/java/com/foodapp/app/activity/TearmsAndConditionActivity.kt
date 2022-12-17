 package com.foodapp.app.activity

import android.view.View
import android.webkit.WebViewClient
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.activity_privacy_policy.ivBack
import kotlinx.android.synthetic.main.activity_search.*

 class TearmsAndConditionActivity:BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_terms_and_condition

    override fun InitView() {
        ivBack.setOnClickListener {
            finish()
        }
        webView.setWebViewClient(WebViewClient())
        webView.getSettings().setLoadsImagesAutomatically(true)
        webView.getSettings().setJavaScriptEnabled(true)
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        webView.loadUrl(ApiClient.termscondition)

        if(SharePreference.getStringPref(this@TearmsAndConditionActivity, SharePreference.SELECTED_LANGUAGE)
                .equals(resources.getString(R.string.language_hindi))){
            ivBack.rotation= 180F
        }else{
            ivBack.rotation= 0F
        }
    }
}