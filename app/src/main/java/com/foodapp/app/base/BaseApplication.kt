package com.foodapp.app.base


import android.app.Application
import androidx.multidex.MultiDex
import com.foodapp.app.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Poppins-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .disableCustomViewInflation()
                        .build()
        )
        MultiDex.install(this);
    }
}