package com.foodapp.app.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.api.*
import com.foodapp.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_forgetpassword.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ForgetPasswordActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_forgetpassword
    }

    override fun InitView() {
        Common.getCurrentLanguage(this@ForgetPasswordActivity, false)
        if (SharePreference.getStringPref(this@ForgetPasswordActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.tvSubmit -> {
                if (edEmail.text.toString().equals("")) {
                    Common.showErrorFullMsg(this@ForgetPasswordActivity, resources.getString(R.string.validation_all))
                } else if (!Common.isValidEmail(edEmail.text.toString())) {
                    Common.showErrorFullMsg(this@ForgetPasswordActivity, resources.getString(R.string.validation_valid_email))
                } else {
                    val hasmap = HashMap<String, String>()
                    hasmap.put("email", edEmail.text.toString())
                    if (Common.isCheckNetwork(this@ForgetPasswordActivity)) {
                        callApiForgetpassword(hasmap)
                    } else {
                        Common.alertErrorOrValidationDialog(
                                this@ForgetPasswordActivity,
                                resources.getString(R.string.no_internet)
                        )
                    }
                }
            }
            R.id.tvSignup -> {
                openActivity(RegistrationActivity::class.java)
                finish()
                finishAffinity()
            }
        }
    }

    private fun callApiForgetpassword(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ForgetPasswordActivity)
        val call = ApiClient.getClient.setforgotPassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {

                        successfulDialog(
                                this@ForgetPasswordActivity,
                                restResponse.getMessage()
                        )
                    }
                    else {
                        successfulDialog(
                                this@ForgetPasswordActivity,
                                restResponse.getMessage()
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(this@ForgetPasswordActivity, resources.getString(R.string.error_msg))
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(this@ForgetPasswordActivity, resources.getString(R.string.error_msg))
            }
        })
    }

    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {

            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                finish()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ForgetPasswordActivity, false)
    }
}