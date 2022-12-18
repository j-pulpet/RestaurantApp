package com.foodapp.app.activity

import android.content.Intent
import android.net.Uri
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.model.Admin
import com.foodapp.app.model.GetProfileResponse
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_contact_us.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ContactUsActivity : BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_contact_us

    override fun InitView() {
        val adminResponse = SharePreference.getStringPref(this@ContactUsActivity, SharePreference.admin)
        val adminData = Gson().fromJson(adminResponse, Admin::class.java)
        setProfileData(adminData)

        ivBack?.setOnClickListener {
            finish()
        }
        linerPhone.setOnClickListener {
            openPhone(adminData?.mobile.toString())
        }

        linearEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(adminData?.email.toString()))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }


        ivFacebook.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(adminData?.fb)
            startActivity(i)
        }

        ivInstagram.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(adminData?.insta)
            startActivity(i)
        }

        ivTwitter.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(adminData?.twitter)
            startActivity(i)
        }
        btnSubmit?.setOnClickListener {
            when {
                tvFirstName.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                            this@ContactUsActivity,
                            resources.getString(R.string.validation_all)
                    )

                }
                tvLastName.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                            this@ContactUsActivity,
                            resources.getString(R.string.validation_all)
                    )

                }
                edEmail.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                            this@ContactUsActivity,
                            resources.getString(R.string.validation_all)
                    )

                }
                edMessage.text.isEmpty() -> {
                    Common.showErrorFullMsg(
                            this@ContactUsActivity,
                            resources.getString(R.string.validation_all)
                    )

                }
                else -> {
                    val contactUsRequest = HashMap<String, String>()
                    contactUsRequest["firstname"] = tvFirstName.text.toString()
                    contactUsRequest["email"] = edEmail.text.toString()
                    contactUsRequest["lastname"] = tvLastName.text.toString()
                    contactUsRequest["message"] = edMessage.text.toString()
                    callApiContactUS(contactUsRequest)
                }
            }
        }


    }


    private fun openPhone(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        startActivity(intent)
    }

    private fun callApiProfile(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ContactUsActivity)
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<GetProfileResponse> {
            override fun onResponse(
                    call: Call<GetProfileResponse>,
                    response: Response<GetProfileResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: GetProfileResponse = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        val dataResponse: Admin = restResponce.admin!!
                    } else if (restResponce.data!!.equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                                this@ContactUsActivity,
                                restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@ContactUsActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setProfileData(dataResponse: Admin) {
        tvPhoneNumber!!.setText(dataResponse.mobile)
        tvEmailAddress!!.setText(dataResponse.email)
        tvAddress!!.text = dataResponse.address

    }

    private fun callApiContactUS(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ContactUsActivity)
        val call = ApiClient.getClient.contactUs(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                    call: Call<SingleResponse>,
                    response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: SingleResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        Common.showSuccessFullMsg(this@ContactUsActivity, restResponce.getMessage().toString())
                        finish()
                    } else if (restResponce.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@ContactUsActivity, restResponce.getMessage())
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(this@ContactUsActivity, resources.getString(R.string.error_msg))
            }
        })
    }
}