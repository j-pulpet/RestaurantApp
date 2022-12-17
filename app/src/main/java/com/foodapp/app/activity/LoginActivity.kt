package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.foodapp.app.R
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.model.LoginModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.setStringPref
import com.foodapp.app.utils.SharePreference.Companion.userEmail
import com.foodapp.app.utils.SharePreference.Companion.userId
import com.foodapp.app.utils.SharePreference.Companion.userMobile
import com.foodapp.app.api.*
import com.foodapp.app.model.RegistrationModel
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.Common.getLog
import com.foodapp.app.utils.Common.showErrorFullMsg
import com.foodapp.app.utils.SharePreference.Companion.setBooleanPref
import com.foodapp.app.utils.SharePreference.Companion.userName
import com.foodapp.app.utils.SharePreference.Companion.userProfile
import com.foodapp.app.utils.SharePreference.Companion.userRefralCode
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity:BaseActivity() {

    //:::::::::::::::Google Login::::::::::::::::://
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 1

    //:::::::::::::::Facebook Login::::::::::::::::://
    private var callbackManager: CallbackManager? = null
    var callback: FacebookCallback<LoginResult>?=null

    var strToken=""
    override fun setLayout(): Int {
        return R.layout.activity_login
    }
    override fun InitView() {
        Common.getCurrentLanguage(this@LoginActivity, false)
        FirebaseApp.initializeApp(this@LoginActivity)
        strToken=FirebaseInstanceId.getInstance().token.toString()
        getLog("Token== ",strToken)

        //:::::::::::::::Google Login::::::::::::::::://
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        rlbtnGoogle.setOnClickListener {
            if (Common.isCheckNetwork(this@LoginActivity)) {
              mGoogleSignInClient!!.signOut().addOnCompleteListener(this, object : OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
                    signInGoogle()
                }
              })
            } else {
              alertErrorOrValidationDialog(
                  this@LoginActivity,
                  resources.getString(R.string.no_internet)
              )
            }
        }


        //::::::::::::::Facebook Login::::::::::::::::://
        FacebookSdk.setApplicationId("970149196792518");
        FacebookSdk.sdkInitialize(this@LoginActivity)
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
          .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                updateFacebookUI(loginResult)
            }

             override fun onCancel() {}
             override fun onError(error: FacebookException) {
                 Toast.makeText(applicationContext, "" + error.message, Toast.LENGTH_LONG)
                         .show()
             }
          })

        rlbtnFacebook.setOnClickListener {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut()
            }
            LoginManager
                    .getInstance()
                    .logInWithReadPermissions(
                            this,
                            getFacebookPermissions()
                    )
        }

    }

    fun getFacebookPermissions(): List<String> {
        return listOf("email")
    }

    fun onClick(v: View?) {
        when (v!!.id) {
           R.id.tvLogin->{
              if (edEmail.text.toString().equals("")) {
                  showErrorFullMsg(this@LoginActivity,resources.getString(R.string.validation_all))
              } else if (!Common.isValidEmail(edEmail.text.toString())) {
                  showErrorFullMsg(this@LoginActivity,resources.getString(R.string.validation_valid_email))
              } else if (edPassword.text.toString().equals("")) {
                  showErrorFullMsg(this@LoginActivity,resources.getString(R.string.validation_all))
              } else {
                val hasmap = HashMap<String, String>()
                hasmap["email"] = edEmail.text.toString()
                hasmap["password"] = edPassword.text.toString()
                hasmap["token"] = strToken
                if (Common.isCheckNetwork(this@LoginActivity)) {
                  callApiLogin(hasmap)
                } else {
                  Common.alertErrorOrValidationDialog(this@LoginActivity,resources.getString(R.string.no_internet))
                }
              }
           }
           R.id.tvSignup->{
               openActivity(RegistrationActivity::class.java)
           }
           R.id.tvForgetPassword->{
               openActivity(ForgetPasswordActivity::class.java)
           }
           R.id.tvSkip->{
               openActivity(DashboardActivity::class.java)
               finish()
               finishAffinity()
           }

        }
    }


    private fun callApiLogin(hasmap: HashMap<String, String>) {
        showLoadingProgress(this@LoginActivity)
        val call = ApiClient.getClient.getLogin(hasmap)
        call.enqueue(object : Callback<RestResponse<LoginModel>> {
            override fun onResponse(
                call: Call<RestResponse<LoginModel>>,
                response: Response<RestResponse<LoginModel>>
            ) {
                if(response.code()==200){
                   val loginResponce: RestResponse<LoginModel> = response.body()!!
                   if (loginResponce.getStatus().equals("1")) {
                       Common.dismissLoadingProgress()
                       val loginModel: LoginModel = loginResponce.getData()!!
                       SharePreference.setBooleanPref(this@LoginActivity, SharePreference.isLogin,true)
                       setStringPref(this@LoginActivity,userId, loginModel.getId()!!)
                       setStringPref(this@LoginActivity,userMobile, loginModel.getMobile()!!)
                       setStringPref(this@LoginActivity,userEmail, loginModel.getEmail()!!)
                       val intent = Intent(this@LoginActivity,DashboardActivity::class.java)
                       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                       startActivity(intent);
                       finish()
                       finishAffinity()
                   }
                } else  {
                    val error=JSONObject(response.errorBody()!!.string())
                    val status=error.getInt("status")
                    if(status==2){
                        Common.dismissLoadingProgress()
                        startActivity(Intent(this@LoginActivity,OTPVerificatinActivity::class.java).putExtra("email", edEmail.text.toString()))
                    }else{
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(this@LoginActivity,error.getString("message"))
                    }

                }
            }

            override fun onFailure(call: Call<RestResponse<LoginModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@LoginActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@LoginActivity, false)
    }

    override fun onBackPressed() {
        finish()
        finishAffinity()
    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)!!
            nextGmailActivity(account)
        } catch (e: ApiException) {
            Log.e("Google Login", "signInResult:failed code=" + e.statusCode)
        }
    }


    @SuppressLint("HardwareIds")
    private fun nextGmailActivity(profile: GoogleSignInAccount?) {
        if (profile != null) {
            val loginType = "google"
            val FristName = profile.displayName
            val profileEmail = profile.email
            val profileId = profile.id
            loginApiCall(FristName!!, profileEmail!!, profileId!!, loginType, strToken)
        }
    }

    private fun mGoToRegistration(name: String, profileEmail: String, profileId: String, loginType: String, strToken: String) {
        val intent=Intent(this@LoginActivity, RegistrationActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("profileEmail", profileEmail)
        intent.putExtra("profileId", profileId)
        intent.putExtra("loginType", loginType)
        intent.putExtra("strToken", strToken)
        startActivity(intent)
    }

    //::::::::::::::FacebookLogin:::::::::::::://
    private fun updateFacebookUI(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(loginResult.accessToken, object : GraphRequest.GraphJSONObjectCallback {
            override fun onCompleted(
                    `object`: JSONObject,
                    response: GraphResponse?
            ) {
                getFacebookData(`object`)
            }
        })
        val parameters = Bundle()
        parameters.putString(
                "fields",
                "id, first_name, last_name, email,age_range, gender, birthday, location"
        ) // Parámetros que pedimos a facebook
        request.parameters = parameters
        request.executeAsync()
    }

    private fun getFacebookData(`object`: JSONObject) {
        try {
            val profileId = `object`.getString("id")
            var name = ""
            if (`object`.has("first_name")) {
                name = `object`.getString("first_name")
            }
            if (`object`.has("last_name")) {
                name += " " + `object`.getString("last_name")
            }
            var email = ""
            if (`object`.has("email")){
                email = `object`.getString("email")
            }
            val loginType = "facebook"
            loginApiCall(name,email,profileId,loginType,strToken)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun loginApiCall(name: String, email: String, profileId: String, loginType: String, strToken: String) {
        val hasmap= HashMap<String, String>()
        hasmap["name"] = name
        hasmap["email"] = email
        hasmap["mobile"] = ""
        hasmap["token"] = strToken
        hasmap["login_type"] = loginType
        if(loginType=="google"){
            hasmap["google_id"]=profileId
            hasmap["facebook_id"]=""
        }else{
            hasmap["facebook_id"]=profileId
            hasmap["google_id"]=""
        }
        showLoadingProgress(this@LoginActivity)
        val call = ApiClient.getClient.setRegistration(hasmap)
        call.enqueue(object : Callback<RestResponse<RegistrationModel>> {
            override fun onResponse(
                    call: Call<RestResponse<RegistrationModel>>,
                    response: Response<RestResponse<RegistrationModel>>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: RestResponse<RegistrationModel> = response.body()!!
                    if (registrationResponse.getStatus().toString().equals("1")) {
                        dismissLoadingProgress()
                        setProfileData(registrationResponse.getData(),registrationResponse.getMessage())
                    } else if (registrationResponse.getStatus().toString().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(this@LoginActivity, registrationResponse.getMessage())
                    }else if (registrationResponse.getStatus().toString().equals("2")) {
                        dismissLoadingProgress()
                        mGoToRegistration(name, email, profileId, loginType, strToken)
                    }else if (registrationResponse.getStatus().toString().equals("3")) {
                        dismissLoadingProgress()
                        startActivity(Intent(this@LoginActivity,OTPVerificatinActivity::class.java).putExtra("email",email))
                    }
                }else  {
                    val error= JSONObject(response.errorBody()!!.string())
                    if(error.getString("status")=="3"){
                        dismissLoadingProgress()
                        startActivity(Intent(this@LoginActivity,OTPVerificatinActivity::class.java).putExtra("email",email))
                    }else{
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                                this@LoginActivity,
                                error.getString("message")
                        )
                    }

                }
            }

            override fun onFailure(call: Call<RestResponse<RegistrationModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                        this@LoginActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })

    }

    private fun setProfileData(dataResponse: RegistrationModel?, message: String?) {
        setBooleanPref(this@LoginActivity, SharePreference.isLogin, true)
        setStringPref(this@LoginActivity, userId, dataResponse?.getId().toString())
        setStringPref(this@LoginActivity, userName, dataResponse?.getName().toString())
        setStringPref(this@LoginActivity, userMobile, dataResponse?.getMobile().toString())
        setStringPref(this@LoginActivity, userEmail, dataResponse?.getEmail().toString())
        setStringPref(this@LoginActivity, userProfile, dataResponse?.getProfile_image().toString())
        setStringPref(this@LoginActivity, userRefralCode, dataResponse?.getReferral_code().toString())
        startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))
        finish()
        finishAffinity()
    }

}