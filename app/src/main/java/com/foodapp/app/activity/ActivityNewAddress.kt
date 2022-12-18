package com.foodapp.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodapp.app.R
import com.foodapp.app.adaptor.PinCodeListAdapter
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.FieldSelector
import com.foodapp.app.utils.SharePreference
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.grocery.app.api.ListResopone
import com.grocery.app.model.PinCodeResponse
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_new_address.*
import kotlinx.android.synthetic.main.activity_new_address.ivBack
import kotlinx.android.synthetic.main.dlg_picode.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap

class ActivityNewAddress : BaseActivity() {
    override fun setLayout(): Int = R.layout.activity_new_address
    var type = 0
    private var isClick = false
    private var isFirstTime = false
    private var addressId = 0
    var lat: Double = 0.0
    var lon: Double = 0.0
    var AUTOCOMPLETE_REQUEST_CODE: Int = 2
    var fieldSelector: FieldSelector? = null
    var pinCodeArray = ArrayList<String>()
    val addressTypeArray = arrayListOf<String>()

    private var addressType = 0
    override fun InitView() {

        Places.initialize(
            applicationContext,
            SharePreference.getStringPref(this@ActivityNewAddress, SharePreference.mapKey)!!
        )

        addressTypeArray.add(resources.getString(R.string.home))
        addressTypeArray.add(resources.getString(R.string.work))
        addressTypeArray.add(resources.getString(R.string.other))
        fieldSelector = FieldSelector()
        callGetNeighbourhood()
        tvSaveAddress?.setOnClickListener {
            if (Common.isCheckNetwork(this@ActivityNewAddress)) {
                validation()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActivityNewAddress,
                    resources.getString(R.string.no_internet)
                )

            }
        }


        type = intent.getIntExtra("Type", 0)
        if (type == 1) {
            isClick = true
            tvAddressTitle?.text = resources.getString(R.string.edit_address)
            tvSaveAddress?.text = resources.getString(R.string.update_address)
            getdata()
        } else {
            tvAddressTitle?.text = resources.getString(R.string.new_address)
            tvSaveAddress?.text = resources.getString(R.string.save_address)
//            edtAddress.text = "New York, NY, USA"
//            lat = 40.7127753
//            lon = -74.0059728
//            edtLandMark.setText("Central Park")
//            edtDoorNo.setText("4043")
        }
//        edtAddress.isEnabled = false
//        edtLandMark.isEnabled = false
//        edtDoorNo.isEnabled = false

        tvAddressType.setOnClickListener {
            /*  isClick = true
              spAddressType.performClick()*/
            openSelectAddressTypeCategoryDialog()
        }
        ivBack?.setOnClickListener {
            finish()
        }
        edtZipCode.setOnClickListener {
            if (pinCodeArray.size > 0) {
                openPinCodeDialog()
            }
        }
        edtAddress?.setOnClickListener {
            getLocation()
        }

        /*   val adapter = ArrayAdapter(
                   this@ActivityNewAddress,
                   R.layout.textview_spinner,
                   addressTypeArray
           )
           adapter.setDropDownViewResource(R.layout.textview_spinner)
           spAddressType.adapter = adapter*/


        /*  spAddressType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
              override fun onNothingSelected(parent: AdapterView<*>?) {
                  Log.e("erore", "error")
              }

              override fun onItemSelected(
                      parent: AdapterView<*>?,
                      view: View?,
                      position: Int,
                      id: Long
              ) {
                  if (isClick) {
                      if (position != 0) {
                          tvAddressType.text = addressTypeArray[position]
                      }
                      when (position) {
                          1 -> {
                              addressType = 1
                          }
                          2 -> {
                              addressType = 2

                          }
                          3 -> {
                              addressType = 3

                          }
                      }
                      Log.e("addressType", addressType.toString())
                  }

              }
          }*/

        ivGetLocation.setOnClickListener {
            getLocationPermission()
        }
    }


    private fun getLocationPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        getCurrentLocation()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Common.settingDialog(this@ActivityNewAddress)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }


    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        //now getting address from latitude and longitude

        val geocoder = Geocoder(this@ActivityNewAddress, Locale.getDefault())
        var addresses: List<Address>

        LocationServices.getFusedLocationProviderClient(this@ActivityNewAddress)
            .requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    LocationServices.getFusedLocationProviderClient(this@ActivityNewAddress)
                        .removeLocationUpdates(this)
                    if (locationResult != null && locationResult.locations.size > 0) {
                        val locIndex = locationResult.locations.size - 1

                        val latitude = locationResult.locations[locIndex].latitude
                        val longitude = locationResult.locations[locIndex].longitude
                        lat = latitude
                        lon = longitude

                        addresses = geocoder.getFromLocation(latitude, longitude, 1)

                        val address: String = addresses[0].getAddressLine(0)
                        edtAddress.text = address
                        /*if (tvAddress != null){
                            loader.visibility = View.GONE
                        }*/
                    }
                }
            }, Looper.getMainLooper())

    }

    private fun getLocation() {
        val autocompleteIntent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            fieldSelector!!.allFields
        ).build(this@ActivityNewAddress)
        startActivityForResult(autocompleteIntent, AUTOCOMPLETE_REQUEST_CODE)
    }


    private fun openSelectAddressTypeCategoryDialog() {
        var dialog: Dialog? = null

        dialog?.dismiss()
        dialog = Dialog(this@ActivityNewAddress, R.style.AppCompatAlertDialogStyleBig)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val mInflater = LayoutInflater.from(this@ActivityNewAddress)
        val mView = mInflater.inflate(R.layout.dlg_picode, null, false)
        mView.tvTitle.text = resources.getString(R.string.select_address_type)
        val adapter = PinCodeListAdapter(addressTypeArray) { s: String, i: Int ->
            if (s == "ItemClick") {
                tvAddressType.text = addressTypeArray[i]
                addressType = i + 1
                dialog.dismiss()
            }
        }

        val rvPinCodeList: RecyclerView = mView.findViewById(R.id.rvPinCodeList)
        rvPinCodeList.adapter = adapter
        rvPinCodeList.layoutManager = LinearLayoutManager(this@ActivityNewAddress)
        rvPinCodeList.itemAnimator = DefaultItemAnimator()
        rvPinCodeList.isNestedScrollingEnabled = true


        dialog.setContentView(mView)
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                AutocompleteActivity.RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    edtAddress.text = place.address
                    val latLng: String = place.latLng.toString()
                    val tempArray =
                        latLng.substring(latLng.indexOf("(") + 1, latLng.lastIndexOf(")"))
                            .split(",")
                            .toTypedArray()
                    lat = tempArray[0].toDouble()
                    lon = tempArray[1].toDouble()
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status: Status = Autocomplete.getStatusFromIntent(data!!)
                    Common.showErrorFullMsg(
                        this@ActivityNewAddress,
                        resources.getString(R.string.invalid_key)
                    )
                }
                AutocompleteActivity.RESULT_CANCELED -> {
                    Common.getLog("Nice", " RESULT_CANCELED : AutoComplete Places")
                }
            }
        }
    }

    private fun getdata() {

//        edtFullName.setText(intent.getStringExtra("FullName"))
        addressId = intent.getIntExtra("address_id", 0)
        lat = intent.getStringExtra("lat")?.toDouble() ?: 0.00
        lon = intent.getStringExtra("long")?.toDouble() ?: 0.00
        addressId = intent.getIntExtra("address_id", 0)
        edtAddress.text = intent.getStringExtra("Address")
        edtZipCode.text = intent.getStringExtra("PinCode")
//        edtPhoneNumber.setText(intent.getStringExtra("PhoneNumber"))
        edtDoorNo.setText(intent.getStringExtra("FlatNo"))
        edtLandMark.setText(intent.getStringExtra("landMark"))
        addressType = intent.getIntExtra("addressType", 0)

        when (addressType) {
            1 -> {
                tvAddressType.text = addressTypeArray[0]
            }
            2 -> {
                tvAddressType.text = addressTypeArray[1]

            }
            3 -> {
                tvAddressType.text = addressTypeArray[2]
            }
        }
    }


    private fun callGetNeighbourhood() {
        val call = ApiClient.getClient.getNeighbourhood()
        call.enqueue(object : Callback<ListResopone<PinCodeResponse>> {
            override fun onResponse(
                call: Call<ListResopone<PinCodeResponse>>,
                response: Response<ListResopone<PinCodeResponse>>
            ) {
                if (response.code() == 200) {
                    pinCodeArray.clear()
                    for (i in 0 until response.body()?.data?.size!!) {
                        val array = response.body()?.data
                        pinCodeArray.add(array?.get(i)?.pincode.toString())
                    }

                }
            }

            override fun onFailure(call: Call<ListResopone<PinCodeResponse>>, t: Throwable) {
            }


        })
    }

    private fun openPinCodeDialog() {
        var dialog: Dialog? = null

        dialog?.dismiss()
        dialog = Dialog(this@ActivityNewAddress, R.style.AppCompatAlertDialogStyleBig)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val mInflater = LayoutInflater.from(this@ActivityNewAddress)
        val mView = mInflater.inflate(R.layout.dlg_picode, null, false)
        val adapter = PinCodeListAdapter(pinCodeArray) { s: String, i: Int ->
            if (s == "ItemClick") {
                edtZipCode.text = pinCodeArray[i]
                dialog.dismiss()
            }
        }

        val rvPinCodeList: RecyclerView = mView.findViewById(R.id.rvPinCodeList)
        rvPinCodeList.adapter = adapter
        rvPinCodeList.layoutManager = LinearLayoutManager(this@ActivityNewAddress)
        rvPinCodeList.itemAnimator = DefaultItemAnimator()
        rvPinCodeList.isNestedScrollingEnabled = true


        dialog.setContentView(mView)
        dialog.show()
    }

    private fun validation() {

        when {
//            edtFullName.text.toString() == "" -> {
//                Common.showErrorFullMsg(
//                        this@ActivityNewAddress,
//                        resources.getString(R.string.validation_all)
//                )
//            }


//            edtPhoneNumber.text.toString() == "" -> {
//                Common.showErrorFullMsg(
//                        this@ActivityNewAddress,
//                        resources.getString(R.string.validation_all)
//                )
//            }
            tvAddressType.text.toString() == "" -> {
                Common.showErrorFullMsg(
                    this@ActivityNewAddress,
                    resources.getString(R.string.validation_all)
                )
            }
            edtAddress.text.toString() == "" -> {
                Common.showErrorFullMsg(
                    this@ActivityNewAddress,
                    resources.getString(R.string.validation_all)
                )
            }
            edtDoorNo.text.toString() == "" -> {
                Common.showErrorFullMsg(
                    this@ActivityNewAddress,
                    resources.getString(R.string.validation_all)
                )
            }
            edtLandMark.text.toString() == "" -> {
                Common.showErrorFullMsg(
                    this@ActivityNewAddress,
                    resources.getString(R.string.validation_all)
                )
            }
            edtZipCode.text.toString() == "" -> {
                Common.showErrorFullMsg(
                    this@ActivityNewAddress,
                    resources.getString(R.string.validation_all)
                )
            }
            else -> {
                val request = HashMap<String, String>()
                request["address_type"] = addressType.toString()
                request["address"] = edtAddress.text.toString()
                request["building"] = edtDoorNo.text.toString()
//                request["full_name"] = edtFullName.text.toString()
//                request["mobile"] = edtPhoneNumber.text.toString()
                request["landmark"] = edtLandMark.text.toString()
                request["pincode"] = edtZipCode.text.toString()
                request["lang"] = lon.toString()
                request["lat"] = lat.toString()
                request["user_id"] =
                    SharePreference.getStringPref(this@ActivityNewAddress, SharePreference.userId)
                        ?: ""

                if (type == 1) {
                    request["address_id"] = addressId.toString()
                    Log.e("request", request.toString())
                    callApiUpdateAddress(request)
                } else {
                    callApiAddAddress(request)
                }
            }
        }
    }

    private fun callApiAddAddress(addressRequest: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActivityNewAddress)

        val call = ApiClient.getClient.addAddress(addressRequest)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.getStatus() == "1") {
                        Common.isAddOrUpdated = true
                        finish()
                    } else {
                        Common.showErrorFullMsg(
                            this@ActivityNewAddress,
                            response.body()?.getMessage().toString()
                        )
                    }
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@ActivityNewAddress,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActivityNewAddress,
                    resources.getString(R.string.error_msg)
                )

            }
        })
    }


    private fun callApiUpdateAddress(addressRequest: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActivityNewAddress)


        val call = ApiClient.getClient.updateAddress(addressRequest)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    Common.isAddOrUpdated = true

                    if (response.body()?.getStatus() == "1") {

                        finish()
                    } else {
                        Common.showErrorFullMsg(
                            this@ActivityNewAddress,
                            response.body()?.getMessage().toString()
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActivityNewAddress,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActivityNewAddress,
                    resources.getString(R.string.error_msg)
                )

            }
        })
    }
}