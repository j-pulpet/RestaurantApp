package com.foodapp.app.fragment

import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import com.foodapp.app.BuildConfig
import com.foodapp.app.R
import com.foodapp.app.activity.*
import com.foodapp.app.base.BaseFragmnet
import com.foodapp.app.utils.Common.getCurrentLanguage
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getBooleanPref
import kotlinx.android.synthetic.main.fragment_home.ivMenu
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : BaseFragmnet() {
    override fun setView(): Int {
        return R.layout.fragment_setting
    }

    override fun Init(view: View) {
        getCurrentLanguage(activity!!, false)
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

        ivSelectArabic.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
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
        });

        ivSelectEnglish.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
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
        });

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

        cvBtnEditProfile.setOnClickListener {
            if (SharePreference.getBooleanPref(activity!!, SharePreference.isLogin)) {
                openActivity(EditProfileActivity::class.java)
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
            if (SharePreference.getBooleanPref(activity!!, SharePreference.isLogin)) {
                openActivity(ChangePasswordActivity::class.java)
            } else {
                openActivity(LoginActivity::class.java)
                activity!!.finish()
                activity!!.finishAffinity()
            }
        }
        cvPolicy.setOnClickListener {
            startActivity(Intent(activity,ProvacyPolicyActivity::class.java).putExtra("Policy",true))
        }





        cvAboutUs.setOnClickListener {
            startActivity(Intent(activity,ProvacyPolicyActivity::class.java).putExtra("Policy",false))
        }

       /* try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Food App")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                "${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}".trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
        }*/
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(activity!!, false)
    }


}