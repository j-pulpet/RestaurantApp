package com.foodapp.app.fragment

import android.content.Intent
import android.view.View
import com.foodapp.app.R
import com.foodapp.app.activity.*
import com.foodapp.app.base.BaseFragmnet
import com.foodapp.app.utils.Common.getCurrentLanguage
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getBooleanPref
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import kotlinx.android.synthetic.main.fragment_home.ivMenu
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_setting
    }

    override fun Init(view: View) {
        getCurrentLanguage(activity!!, false)
        val loginType= getStringPref(requireActivity(),SharePreference.loginType)

        if(loginType=="google"||loginType=="facebook" || getStringPref(requireActivity(),SharePreference.UserLoginType)=="1")
        {
            cvBtnPassword.visibility=View.GONE
        }
        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }
        if (SharePreference.getStringPref(
                        activity!!,
                        SharePreference.SELECTED_LANGUAGE
                ) == resources.getString(R.string.language_english)
        ) {
            ivSelectArabic.isChecked = false
            ivSelectEnglish.isChecked = true
        } else {
            ivSelectArabic.isChecked = true
            ivSelectEnglish.isChecked = false
        }

        ivSelectArabic.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                ivSelectArabic.isChecked = true
                ivSelectEnglish.isChecked = false
                SharePreference.setStringPref(
                        activity!!,
                        SharePreference.SELECTED_LANGUAGE,
                        activity!!.resources.getString(R.string.language_hindi)
                )
                getCurrentLanguage(activity!!, true)
            }
        }

        ivSelectEnglish.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                ivSelectArabic.isChecked = false
                ivSelectEnglish.isChecked = true
                SharePreference.setStringPref(
                        activity!!,
                        SharePreference.SELECTED_LANGUAGE,
                        activity!!.resources.getString(R.string.language_english)
                )
                getCurrentLanguage(activity!!, true)
            }
        }

        llArabic.setOnClickListener {
            ivSelectArabic.isChecked = true
            ivSelectEnglish.isChecked = false
            SharePreference.setStringPref(
                    activity!!,
                    SharePreference.SELECTED_LANGUAGE,
                    activity!!.resources.getString(R.string.language_hindi)
            )
            getCurrentLanguage(activity!!, true)
        }
        llEnglish.setOnClickListener {
            ivSelectArabic.isChecked = false
            ivSelectEnglish.isChecked = true
            SharePreference.setStringPref(
                    activity!!,
                    SharePreference.SELECTED_LANGUAGE,
                    activity!!.resources.getString(R.string.language_english)
            )
            getCurrentLanguage(activity!!, true)
        }

        cvContactUs?.setOnClickListener {
            if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                openActivity(ContactUsActivity::class.java)
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }

        cvBtnEditProfile.setOnClickListener {
            if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                openActivity(EditProfileActivity::class.java)
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }

        cvManageAddresses.setOnClickListener {
            if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                val intent = Intent(requireContext(), GetAddressActivity::class.java)
                intent.putExtra("isComeFromSelectAddress", false)
                startActivity(intent)
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }
        cvRefarAndEarn.setOnClickListener {
            if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                openActivity(RefarAndEarnActivity::class.java)
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }

        cvBtnPassword.setOnClickListener {
            if (getBooleanPref(activity!!, SharePreference.isLogin)) {
                openActivity(ChangePasswordActivity::class.java)
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }
        cvPolicy.setOnClickListener {
            startActivity(
                    Intent(activity, ProvacyPolicyActivity::class.java).putExtra(
                            "Policy",
                            true
                    )
            )
        }

        cvAboutUs.setOnClickListener {
            startActivity(
                    Intent(activity, ProvacyPolicyActivity::class.java).putExtra(
                            "Policy",
                            false
                    )
            )
        }

    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(activity!!, false)
    }


}