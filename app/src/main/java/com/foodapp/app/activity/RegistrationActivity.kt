package com.foodapp.app.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.model.RegistrationModel
import com.foodapp.app.utils.Common
import com.foodapp.app.api.*
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.showErrorFullMsg
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity :BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_registration
    }

    override fun InitView() {
        Common.getCurrentLanguage(this@RegistrationActivity, false)
        tvTermsAndCondition.setOnClickListener {
           openActivity(TearmsAndConditionActivity::class.java)
        }

        if(intent.getStringExtra("loginType")!=null){
            edFullName.setText(intent.getStringExtra("name")!!)
            edEmail.setText(intent.getStringExtra("profileEmail")!!)
            Password.visibility= View.GONE
            edEmail.isActivated=false
            edEmail.setInputType(InputType.TYPE_NULL)
        }else{
            Password.visibility= View.VISIBLE
        }
    }
    fun onClick(v: View?) {
        when (v!!.id) {
           R.id.tvLogin->{
               openActivity(LoginActivity::class.java)
           }
           R.id.tvSignup->{
               if(intent.getStringExtra("loginType")!=null){
                   if(edMobile.text.toString().equals("")){
                       showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                   } else if(intent.getStringExtra("loginType")=="facebook"||intent.getStringExtra("loginType")=="google"){
                       val hasmap= HashMap<String, String>()
                       hasmap["name"] = intent.getStringExtra("name")!!
                       hasmap["email"] = intent.getStringExtra("profileEmail")!!
                       hasmap["mobile"] = edMobile.text.toString()
                       hasmap["referral_code"] = edRefralcode.text.toString()
                       hasmap["token"] = intent.getStringExtra("strToken")!!
                       hasmap["register_type"] = "email"
                       hasmap["login_type"] = intent.getStringExtra("loginType")!!
                       if(intent.getStringExtra("loginType")=="google"){
                           hasmap["google_id"]=intent.getStringExtra("profileId")!!
                           hasmap["facebook_id"]=""
                       }else{
                           hasmap["facebook_id"]=intent.getStringExtra("profileId")!!
                           hasmap["google_id"]=""
                       }
                       if(Common.isCheckNetwork(this@RegistrationActivity)){
                           if(cbCheck.isChecked){
                               callApiRegistration(hasmap)
                           }else{
                               showErrorFullMsg(this@RegistrationActivity,"Please accept our terms and conditions")
                           }
                       }else{
                           alertErrorOrValidationDialog(this@RegistrationActivity,resources.getString(R.string.no_internet))
                       }
                   }
               }else{
                   var strToken=""
                   FirebaseApp.initializeApp(this@RegistrationActivity)
                   strToken= FirebaseInstanceId.getInstance().token.toString()
                   if(edFullName.text.toString().equals("")){
                      showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                   }else if(edEmail.text.toString().equals("")){
                      showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                   }else if(!Common.isValidEmail(edEmail.text.toString())){
                      showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_valid_email))
                   }else if(edMobile.text.toString().equals("")){
                      showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                   }else if(edPassword.text.toString().equals("")){
                      showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
                   }else{
                       if(cbCheck.isChecked){
                           val hasmap= HashMap<String, String>()
                           hasmap["name"] = edFullName.text.toString()
                           hasmap["email"] = edEmail.text.toString()
                           hasmap["mobile"] = edMobile.text.toString()
                           hasmap["password"] = edPassword.text.toString()
                           hasmap["token"] = strToken
                           hasmap["login_type"] = "email"
                           hasmap["register_type"] = "email"
                           hasmap["referral_code"] = edRefralcode.text.toString()
                           if(Common.isCheckNetwork(this@RegistrationActivity)){
                               callApiRegistration(hasmap)
                           }else{
                                alertErrorOrValidationDialog(this@RegistrationActivity,resources.getString(R.string.no_internet))
                           }
                       }else{
                          showErrorFullMsg(this@RegistrationActivity,"Please accept our terms and conditions")
                       }

                   }
               }

           }
           R.id.tvSkip->{
               openActivity(DashboardActivity::class.java)
               finish()
               finishAffinity()
           }

        }
    }
    private fun callApiRegistration(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@RegistrationActivity)
        val call = ApiClient.getClient.setRegistration(hasmap)
        call.enqueue(object : Callback<RestResponse<RegistrationModel>> {
            override fun onResponse(
                call: Call<RestResponse<RegistrationModel>>,
                response: Response<RestResponse<RegistrationModel>>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: RestResponse<RegistrationModel> = response.body()!!
                    if(registrationResponse.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        startActivity(Intent(this@RegistrationActivity,OTPVerificatinActivity::class.java).putExtra("email", edEmail.text.toString()))
                    }else if (registrationResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@RegistrationActivity,
                            registrationResponse.getMessage()
                        )
                    }
                }else  {
                    val error=JSONObject(response.errorBody()!!.string())
                    val status=error.getInt("status")
                    if(status==2){
                        Common.dismissLoadingProgress()
                        startActivity(Intent(this@RegistrationActivity,OTPVerificatinActivity::class.java).putExtra("email", edEmail.text.toString()))
                    }else{
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(this@RegistrationActivity,error.getString("message"))
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<RegistrationModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@RegistrationActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
           if (dialog != null) {
               dialog.dismiss()
           }
           dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
           dialog.window!!.setLayout(
               WindowManager.LayoutParams.MATCH_PARENT,
               WindowManager.LayoutParams.MATCH_PARENT
           );
           dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
           dialog.setCancelable(false)
           val m_inflater = LayoutInflater.from(act)
           val m_view = m_inflater.inflate(R.layout.dlg_validation, null, false)
           val textDesc: TextView = m_view.findViewById(R.id.tvMessage)
           textDesc.text = msg
           val tvOk: TextView = m_view.findViewById(R.id.tvOk)
           val finalDialog: Dialog = dialog
           tvOk.setOnClickListener {
              finalDialog.dismiss()
              startActivity(Intent(this@RegistrationActivity,OTPVerificatinActivity::class.java).putExtra("email", edEmail.text.toString()))
           }
           dialog.setContentView(m_view)
           dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@RegistrationActivity, false)
    }
}