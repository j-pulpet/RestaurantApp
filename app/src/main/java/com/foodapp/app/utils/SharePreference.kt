package com.foodapp.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharePreference {

    companion object {
        lateinit var mContext: Context
        lateinit var sharedPreferences: SharedPreferences
        val PREF_NAME: String = "FoodApp"
        val PRIVATE_MODE: Int = 0
        lateinit var editor: SharedPreferences.Editor
        val isLogin: String = "isLogin"
        val loginToken: String = "logintoken"
        val userId: String = "userid"
        val userMobile: String = "usermobile"
        val loginType: String = "loginType"
        val userEmail: String = "useremail"
        val userName: String = "userName"
        val adminMobile: String = "adminMobile"
        val adminAddress: String = "adminAddress"
        val adminEmail: String = "adminEmail"
        val userProfile: String = "userprofile"
        val isTutorial = "tutorial"
        val isFavourite = "favourite"
        val isCurrancy = "Currancy"
        val isMiniMum = "MiniMum"
        val isMaximum = "Maximum"
        val isMiniMumQty = "isMiniMumQty"
        val mapKey = "mapkey"
        val admin = "admin"
        val isLinearLayoutManager = "Grid"
        val userRefralCode: String = "referral_code"
        val userRefralAmount: String = "referral_amount"
        var SELECTED_LANGUAGE = "selected_language"
        var isMobileLogin = "isMobileLogin"
        var isEmailLogin = "isEmailLogin"
        var UserLoginType = "isEmailLogin"
        fun getIntPref(context: Context, key: String): Int {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            return pref.getInt(key, -1)
        }

        fun setIntPref(context: Context, key: String, value: Int) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            pref.edit().putInt(key, value).apply()
        }

        fun getStringPref(context: Context, key: String): String? {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            return pref.getString(key, "")
        }

        fun setStringPref(context: Context, key: String, value: String) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            pref.edit().putString(key, value).apply()
        }

        fun getBooleanPref(context: Context, key: String): Boolean {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            return pref.getBoolean(key, false)
        }

        fun setBooleanPref(context: Context, key: String, value: Boolean) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            pref.edit().putBoolean(key, value).apply()
        }

    }

    @SuppressLint("CommitPrefEdits")
    constructor(mContext1: Context) {
        mContext = mContext1
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }

    fun mLogout() {
        editor.clear()
        editor.commit()
    }

}