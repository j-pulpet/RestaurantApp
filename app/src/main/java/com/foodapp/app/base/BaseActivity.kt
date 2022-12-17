package com.foodapp.app.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foodapp.app.R
import com.foodapp.app.utils.Common
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


abstract class BaseActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Common.getCurrentLanguage(this@BaseActivity, false)
        setContentView(setLayout())
        InitView()
    }

    protected abstract fun setLayout(): Int
    protected abstract fun InitView()

    open fun openActivity(destinationClass: Class<*>) {
        startActivity(Intent(this@BaseActivity, destinationClass))
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
        Common.getCurrentLanguage(this@BaseActivity, false)
    }
}