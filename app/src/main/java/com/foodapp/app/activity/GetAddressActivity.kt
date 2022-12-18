package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.AddressResponse
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import com.grocery.app.api.ListResopone
import kotlinx.android.synthetic.main.activity_get_address.*
import kotlinx.android.synthetic.main.activity_get_address.ivBack
import kotlinx.android.synthetic.main.activity_get_address.tvNoDataFound
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GetAddressActivity : BaseActivity() {
    var addressList = ArrayList<AddressResponse>()
    var addressAdapter: BaseAdaptor<AddressResponse>? = null
    override fun setLayout(): Int = R.layout.activity_get_address

    override fun InitView() {
        setupGetAddressAdapter(addressList)
        callApiGetAddressList()


        ivBack?.setOnClickListener {
            finish()
        }
        ivAdd?.setOnClickListener {
            val intent = Intent(this@GetAddressActivity, ActivityNewAddress::class.java)
            intent.putExtra("Type", 0)
            startActivity(intent)
        }
    }

    private var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP
            ) {
        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val position = viewHolder.adapterPosition
            addressList.removeAt(position)
            addressAdapter?.notifyDataSetChanged()
        }
    }


    private fun callApiGetAddressList() {
        Common.showLoadingProgress(this@GetAddressActivity)
        val map = HashMap<String, String>()
        map["user_id"] = SharePreference.getStringPref(
                this@GetAddressActivity,
                SharePreference.userId
        )!!
        val call = ApiClient.getClient.getAddress(map)
        call.enqueue(object : Callback<ListResopone<AddressResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                    call: Call<ListResopone<AddressResponse>>,
                    response: Response<ListResopone<AddressResponse>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResopone<AddressResponse> = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        restResponce.data?.let { addressList.addAll(it) }
                        if (addressList.size > 0) {
                            tvNoDataFound.visibility = View.GONE
                            rvAddressList.visibility = View.VISIBLE
                            addressAdapter?.notifyDataSetChanged()
                        } else {
                            tvNoDataFound.visibility = View.VISIBLE
                            rvAddressList.visibility = View.GONE
                        }


                    } else if (restResponce.status == 0) {
                        tvNoDataFound.visibility = View.GONE
                        rvAddressList.visibility = View.VISIBLE
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                                this@GetAddressActivity,
                                restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ListResopone<AddressResponse>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@GetAddressActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (Common.isAddOrUpdated) {
            Common.isAddOrUpdated = false
            addressList.clear()
            callApiGetAddressList()
        }
    }

    private fun setupGetAddressAdapter(addressList: ArrayList<AddressResponse>) {
        addressAdapter =
                object : BaseAdaptor<AddressResponse>(this@GetAddressActivity, addressList) {
                    override fun onBindData(
                            holder: RecyclerView.ViewHolder?,
                            `val`: AddressResponse,
                            position: Int
                    ) {
                        val tvAddressType: TextView = holder!!.itemView.findViewById(R.id.tvType)
                        val tvAddress: TextView = holder.itemView.findViewById(R.id.tvAddress)
                        val tvLandMark: TextView = holder.itemView.findViewById(R.id.tvLandMark)
                        val tvFlatNo: TextView = holder.itemView.findViewById(R.id.tvflatNo)
                        val tvZipCode: TextView = holder.itemView.findViewById(R.id.tvZipCode)
                        val ivEdit: ImageView = holder.itemView.findViewById(R.id.ivEdit)
                        val ivDelete: ImageView = holder.itemView.findViewById(R.id.ivDelete)

                        val addressTypeArray = arrayOf(resources.getString(R.string.home), resources.getString(R.string.work), resources.getString(R.string.other))
                        when (addressList[position].addressType) {
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

                        tvZipCode.text = addressList[position].pincode
                        tvFlatNo.text = addressList[position].building
                        tvLandMark.text = addressList[position].landmark
                        tvAddress.text = addressList[position].address

                        ivDelete.setOnClickListener {

                            if (Common.isCheckNetwork(this@GetAddressActivity)) {
                                dlgDeleteConformationDialog(
                                        this@GetAddressActivity,
                                        resources.getString(R.string.delete_address_alert),
                                        addressList[position].id.toString(),
                                        position
                                )
                            } else {
                                Common.alertErrorOrValidationDialog(
                                        this@GetAddressActivity,
                                        resources.getString(R.string.no_internet)
                                )
                            }
                        }



                        ivEdit.setOnClickListener {
                            val intent = Intent(this@GetAddressActivity, ActivityNewAddress::class.java)
                            intent.putExtra("FullName", addressList[position].fullName)
                            intent.putExtra("PinCode", addressList[position].pincode)
                            intent.putExtra("DeliveryCharge", addressList[position].deliveryCharge)
                            intent.putExtra("PhoneNumber", addressList[position].mobile)
                            intent.putExtra("FlatNo", addressList[position].building)
                            intent.putExtra("landMark", addressList[position].landmark)
                            intent.putExtra("Address", addressList[position].address)
                            intent.putExtra("addressType", addressList[position].addressType)
                            intent.putExtra("address_id", addressList[position].id)
                            intent.putExtra("Type", 1)
                            startActivity(intent)

                        }
                        val isComeFromSelectAddress = intent.getBooleanExtra("isComeFromSelectAddress", false)
                        holder.itemView.setOnClickListener {
                            if (isComeFromSelectAddress) {
                                val intent = Intent(this@GetAddressActivity, OrderSummuryActivity::class.java)
                                intent.putExtra("PinCode", addressList[position].pincode)
                                intent.putExtra("FlatNo", addressList[position].building)
                                intent.putExtra("landMark", addressList[position].landmark)
                                intent.putExtra("DeliveryCharge", addressList[position].deliveryCharge)
                                intent.putExtra("Address", addressList[position].address)
                                intent.putExtra("addressType", addressList[position].addressType)
                                intent.putExtra("address_id", addressList[position].id)
                                intent.putExtra("lat", addressList[position].lat)
                                intent.putExtra("long", addressList[position].lang)
                                intent.putExtra("Type", 1)
                                setResult(500, intent)
                                finish()
                            }
                        }

                    }

                    override fun setItemLayout(): Int = R.layout.cell_address_list

                }
        rvAddressList.apply {
            adapter = addressAdapter
            layoutManager = LinearLayoutManager(this@GetAddressActivity)
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(this)
        }
    }


    @SuppressLint("InflateParams")
    fun dlgDeleteConformationDialog(act: Activity, msg: String?, strCartId: String, pos: Int) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvDesc)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvYes)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                if (Common.isCheckNetwork(this@GetAddressActivity)) {
                    finalDialog.dismiss()
                    val hashMap = HashMap<String, String>()

                    hashMap["user_id"] = SharePreference.getStringPref(
                            this@GetAddressActivity,
                            SharePreference.userId
                    ).toString()
                    hashMap["address_id"] = strCartId
                    callDeleteApi(hashMap, pos)
                } else {
                    Common.alertErrorOrValidationDialog(
                            this@GetAddressActivity,
                            resources.getString(R.string.no_internet)
                    )
                }
            }
            val tvCancle: TextView = mView.findViewById(R.id.tvNo)
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callDeleteApi(deleteRequest: HashMap<String, String>, pos: Int) {
        Common.showLoadingProgress(this@GetAddressActivity)

        val call = ApiClient.getClient.deleteAddress(deleteRequest)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                    call: Call<SingleResponse>,
                    response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.getStatus() == "1") {

                        Common.showSuccessFullMsg(
                                this@GetAddressActivity,
                                response.body()?.getMessage().toString()
                        )
                        addressList.removeAt(pos)

                        addressAdapter?.notifyDataSetChanged()

                        if (addressList.size > 0) {
                            tvNoDataFound?.visibility = View.GONE
                            rvAddressList?.visibility = View.VISIBLE
                        } else {
                            tvNoDataFound?.visibility = View.VISIBLE
                            rvAddressList?.visibility = View.GONE
                        }
                    } else {
                        Common.showErrorFullMsg(
                                this@GetAddressActivity,
                                response.body()?.getMessage().toString()
                        )
                    }
                } else {
                    Common.alertErrorOrValidationDialog(
                            this@GetAddressActivity,
                            resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@GetAddressActivity,
                        resources.getString(R.string.error_msg)
                )

            }
        })
    }
}