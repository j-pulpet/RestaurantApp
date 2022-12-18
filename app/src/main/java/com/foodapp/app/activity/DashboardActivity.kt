package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.setStringPref
import com.foodapp.app.utils.SharePreference.Companion.userName
import com.foodapp.app.utils.SharePreference.Companion.userProfile
import com.foodapp.app.api.*
import com.foodapp.app.fragment.*
import com.foodapp.app.model.*
import com.foodapp.app.utils.SharePreference.Companion.admin
import com.foodapp.app.utils.SharePreference.Companion.adminAddress
import com.foodapp.app.utils.SharePreference.Companion.adminEmail
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dlg_confomation.view.*
import kotlinx.android.synthetic.main.dlg_logout.view.*
import kotlinx.android.synthetic.main.layout_nevheader.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class DashboardActivity : BaseActivity() {
    var drawer_layout: DrawerLayout? = null
    var nav_view: LinearLayout? = null
    var tvName: TextView? = null
    var ivProfile: ImageView? = null
    var temp = 1
    var dataResponse: UserData? = null
    var adminData: Admin? = null
    override fun setLayout(): Int {
        return R.layout.activity_dashboard
    }

    override fun InitView() {
        drawer_layout = findViewById(R.id.drawer_layout)
        nav_view = findViewById(R.id.nav_view)
        tvName = drawer_layout!!.findViewById(R.id.tv_NevProfileName)!!
        ivProfile = drawer_layout!!.findViewById(R.id.ivProfile)!!
        Common.getCurrentLanguage(this@DashboardActivity, false)
        if (SharePreference.getBooleanPref(this@DashboardActivity, SharePreference.isLogin)) {
            rl_Logout.visibility = View.VISIBLE
        } else {
            rl_Logout.visibility = View.GONE
        }

        if (getStringPref(this@DashboardActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))) {
            ivBackHome.rotation = 180F
            ivBackCategory.rotation = 180F
            ivBackFavouriteList.rotation = 180F
            ivBackOrder.rotation = 180F
            ivBackFavouriteList.rotation = 180F
            ivBackRattingAndReview.rotation = 180F
            ivBackWallet.rotation = 180F
            ivBackSetting.rotation = 180F
            ivBackLogout.rotation = 180F
        } else {
            ivBackHome.rotation = 0F
            ivBackCategory.rotation = 0F
            ivBackFavouriteList.rotation = 0F
            ivBackOrder.rotation = 0F
            ivBackFavouriteList.rotation = 0F
            ivBackRattingAndReview.rotation = 0F
            ivBackWallet.rotation = 0F
            ivBackSetting.rotation = 0F
            ivBackLogout.rotation = 0F
        }



        if (intent.getStringExtra("pos") != null) {
            setFragment(intent.getStringExtra("pos")!!.toInt())
            temp = intent.getStringExtra("pos")!!.toInt()
        } else {
            if (SharePreference.getBooleanPref(this@DashboardActivity, SharePreference.isLogin)) {
                if (Common.isCheckNetwork(this@DashboardActivity)) {
                    val hasmap = HashMap<String, String>()
                    hasmap["user_id"] = getStringPref(this@DashboardActivity, SharePreference.userId)!!
                    callApiProfile(hasmap, false)
                } else {
                    Common.alertErrorOrValidationDialog(
                            this@DashboardActivity,
                            resources.getString(R.string.no_internet)
                    )
                }
            } else {
                tvName!!.text = resources.getString(R.string.menu_name)
                ivProfile!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_appicon, null))
                setFragment(1)
                temp = 1
            }
        }

    }


    open fun onDrawerToggle() {
        drawer_layout!!.openDrawer(nav_view!!)
    }

    override fun onBackPressed() {
        mExitDialog()
    }

    fun onClick(v: View?) {
        when (v!!.id) {
            R.id.rl_home -> {
                drawer_layout!!.closeDrawers()
                if (temp != 1) {
                    setFragment(1)
                    temp = 1
                }
            }
            R.id.rl_category -> {
                drawer_layout!!.closeDrawers()
                if (temp != 2) {
                    setFragment(2)
                    temp = 2
                }
            }
            R.id.rl_orderhistory -> {
                drawer_layout!!.closeDrawers()
                if (SharePreference.getBooleanPref(this@DashboardActivity, SharePreference.isLogin)) {
                    if (temp != 3) {
                        setFragment(3)
                        temp = 3
                    }
                } else {
                    openActivity(LoginActivity::class.java)
                    finish()
                }
            }
            R.id.rl_favourite -> {
                drawer_layout!!.closeDrawers()
                if (SharePreference.getBooleanPref(this@DashboardActivity, SharePreference.isLogin)) {
                    if (temp != 4) {
                        setFragment(4)
                        temp = 4
                    }
                } else {
                    openActivity(LoginActivity::class.java)
                    finish()
                }

            }
            R.id.rl_ratting -> {
                drawer_layout!!.closeDrawers()
                if (temp != 5) {
                    setFragment(5)
                    temp = 5
                }

            }
            R.id.rl_wallet -> {
                drawer_layout!!.closeDrawers()
                if (SharePreference.getBooleanPref(this@DashboardActivity, SharePreference.isLogin)) {
                    if (temp != 6) {
                        setFragment(6)
                        temp = 6
                    }
                } else {
                    openActivity(LoginActivity::class.java)
                    finish()
                }
            }
            R.id.rl_setting -> {
                drawer_layout!!.closeDrawers()
                if (temp != 7) {
                    setFragment(7)
                    temp = 7
                }
            }
            R.id.rl_Logout -> {
                drawer_layout!!.closeDrawers()
                setFragment(8)
            }
        }
    }


    @SuppressLint("WrongConstant")
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FramFragment, fragment)
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK)
        fragmentTransaction.commitAllowingStateLoss()
    }


    private fun callApiProfile(hasmap: HashMap<String, String>, isProfile: Boolean) {
        Common.showLoadingProgress(this@DashboardActivity)
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<GetProfileResponse> {
            override fun onResponse(call: Call<GetProfileResponse>, response: Response<GetProfileResponse>) {
                if (response.code() == 200) {
                    val restResponce: GetProfileResponse = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (isProfile) {
                            Common.isProfileMainEdit = false
                        }
                        dataResponse = restResponce.data!!
                        adminData = restResponce.admin!!
                        setStringPref(this@DashboardActivity, userName, dataResponse!!.name!!)
                        setStringPref(this@DashboardActivity, userProfile, dataResponse!!.profileImage!!)
                        setStringPref(this@DashboardActivity, adminAddress, adminData!!.address!!)
                        setStringPref(this@DashboardActivity, adminEmail, adminData!!.email!!)
                        setStringPref(this@DashboardActivity, admin, Gson().toJson(adminData))
                        setProfileData(isProfile)
                    } else if (restResponce.data!!.equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                                this@DashboardActivity,
                                restResponce.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        Common.dismissLoadingProgress()
                        Common.setLogout(this@DashboardActivity)
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                                this@DashboardActivity,
                                error.getString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@DashboardActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setProfileData(
            profile: Boolean
    ) {
        if (SharePreference.getBooleanPref(this@DashboardActivity, SharePreference.isLogin)) {
            tvName!!.text = getStringPref(this@DashboardActivity, userName)
            Glide.with(this@DashboardActivity).load(getStringPref(this@DashboardActivity, userProfile))
                    .placeholder(ResourcesCompat.getDrawable(resources, R.drawable.ic_placeholder, null)).into(ivProfile!!)
            if (!profile) {
                replaceFragment(HomeFragment())
            }
        } else {
            tvName!!.text = resources.getString(R.string.menu_name)
            ivProfile!!.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_appicon, null))
        }

    }

    override fun onStart() {
        super.onStart()
        Common.getCurrentLanguage(this@DashboardActivity, false)
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@DashboardActivity, false)
        if (Common.isProfileMainEdit) {
            if (Common.isCheckNetwork(this@DashboardActivity)) {
                val hasmap = HashMap<String, String>()
                hasmap.put("user_id", getStringPref(this@DashboardActivity, SharePreference.userId)!!)
                callApiProfile(hasmap, true)
            } else {
                Common.alertErrorOrValidationDialog(
                        this@DashboardActivity,
                        resources.getString(R.string.no_internet)
                )
            }
        } else {
            setProfileData(true)
        }
    }

    fun setFragment(pos: Int) {
        when (pos) {
            1 -> {
                replaceFragment(HomeFragment())
            }
            2 -> {
                replaceFragment(CategoriesFragment())
            }
            3 -> {
                replaceFragment(OrderHistoryFragment())
            }
            4 -> {
                replaceFragment(FavouriteFragment())
            }
            5 -> {
                replaceFragment(RattingFragment())
            }
            6 -> {
                replaceFragment(MyWalletFragment())
            }
            7 -> {
                replaceFragment(SettingFragment())
            }
            8 -> {
                alertLogOutDialog()
            }
        }
    }

    private fun alertLogOutDialog() {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
            dialog = Dialog(this@DashboardActivity, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(this@DashboardActivity)
            val mView = mInflater.inflate(R.layout.dlg_logout, null, false)

            val finalDialog: Dialog = dialog
            mView.tvLogout.setOnClickListener {
                finalDialog.dismiss()
                Common.setLogout(this@DashboardActivity)

            }
            mView.tvCancel.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun mExitDialog() {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
            }
            dialog = Dialog(this@DashboardActivity, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(this@DashboardActivity)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)

            val finalDialog: Dialog = dialog
            mView.tvYes.setOnClickListener {
                finalDialog.dismiss()
                ActivityCompat.finishAfterTransition(this@DashboardActivity)
                ActivityCompat.finishAffinity(this@DashboardActivity);
                finish()
            }
            mView.tvNo.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}