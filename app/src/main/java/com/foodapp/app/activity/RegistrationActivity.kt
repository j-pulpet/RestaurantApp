package com.foodapp.app.activity

import `in`.mayanknagwanshi.countrypicker.CountrySelectActivity
import `in`.mayanknagwanshi.countrypicker.bean.CountryData
import android.content.Intent
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.model.RegistrationModel
import com.foodapp.app.utils.Common
import com.foodapp.app.api.*
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.showErrorFullMsg
import com.foodapp.app.utils.SharePreference
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.edCountryCode
import kotlinx.android.synthetic.main.activity_registration.edEmail
import kotlinx.android.synthetic.main.activity_registration.edMobile
import kotlinx.android.synthetic.main.activity_registration.edPassword
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity :BaseActivity() {
    var countryCode="+91"


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
            edEmail.inputType = InputType.TYPE_NULL
        }else{
            Password.visibility= View.VISIBLE
        }

//        if(SharePreference.getStringPref(this@RegistrationActivity,SharePreference.UserLoginType)=="1") {
//            Password.visibility= View.GONE
//        }

        edCountryCode.setOnClickListener {
            val intent = Intent(this, CountrySelectActivity::class.java)
            intent.putExtra(CountrySelectActivity.EXTRA_SELECTED_COUNTRY, CountryData("IN"))
            startActivityForResult(intent, 121)
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
                        hasmap["mobile"] = countryCode.plus(edMobile.text.toString())
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
                                showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.terms_condition_error))
                            }
                        }else{
                            alertErrorOrValidationDialog(this@RegistrationActivity,resources.getString(R.string.no_internet))
                        }
                    }
                }else{
                    var strToken=""
                    FirebaseApp.initializeApp(this@RegistrationActivity)
                    strToken= FirebaseInstanceId.getInstance().token.toString()

//                    if(SharePreference.getStringPref(this@RegistrationActivity,SharePreference.UserLoginType)=="1") {
//
//                        if(edFullName.text.toString().equals("")){
//                            showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
//                        }else if(edEmail.text.toString().equals("")){
//                            showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
//                        }else if(!Common.isValidEmail(edEmail.text.toString())){
//                            showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_valid_email))
//                        }else if(edMobile.text.toString().equals("")){
//                            showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.validation_all))
//                        }else{
//                            if(cbCheck.isChecked){
//                                val hasmap= HashMap<String, String>()
//                                hasmap["name"] = edFullName.text.toString()
//                                hasmap["email"] = edEmail.text.toString()
//                                hasmap["mobile"] =countryCode.plus(edMobile.text.toString())
//                                hasmap["token"] = strToken
//                                hasmap["login_type"] = "email"
//                                hasmap["register_type"] = "email"
//                                hasmap["referral_code"] = edRefralcode.text.toString()
//                                if(Common.isCheckNetwork(this@RegistrationActivity)){
//                                    callApiRegistration(hasmap)
//                                }else{
//                                    alertErrorOrValidationDialog(this@RegistrationActivity,resources.getString(R.string.no_internet))
//                                }
//                            }else{
//                                showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.terms_condition_error))
//                            }
//
//                        }
//
//
//
//                    }
//                    else {



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
                                hasmap["mobile"] =countryCode.plus(edMobile.text.toString())
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
                                showErrorFullMsg(this@RegistrationActivity,resources.getString(R.string.terms_condition_error))
                            }

                        }

//                    }



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
                    var otpController=""
                        otpController = if(SharePreference.getStringPref(this@RegistrationActivity,SharePreference.UserLoginType)=="1") {
                            countryCode.plus(edMobile.text.toString())
                        }else {
                            edEmail.text.toString()
                        }
                        startActivity(Intent(this@RegistrationActivity,OTPVerificatinActivity::class.java).putExtra("email", otpController))
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
                       val otpController = if(SharePreference.getStringPref(this@RegistrationActivity,SharePreference.UserLoginType)=="1") {
                            countryCode.plus(edMobile.text.toString())
                        }else {
                            edEmail.text.toString()
                        }
                        startActivity(Intent(this@RegistrationActivity,OTPVerificatinActivity::class.java).putExtra("email", otpController))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        assert(data != null)
        if (requestCode == 121 && resultCode == RESULT_OK && data!!.hasExtra(CountrySelectActivity.RESULT_COUNTRY_DATA)) {
            val countryData =
                data.getSerializableExtra(CountrySelectActivity.RESULT_COUNTRY_DATA) as CountryData?
//            Toast.makeText(this, countryData!!.co, Toast.LENGTH_SHORT).show()
            edCountryCode.text = countryData?.countryISD
            countryCode = countryData?.countryISD.toString()

            Log.e("Code", countryData?.countryISD + countryData?.countryCode)
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@RegistrationActivity, false)
    }
}