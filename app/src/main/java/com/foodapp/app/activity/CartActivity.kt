package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.ListResponse
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.AddonsModel
import com.foodapp.app.model.CartItemModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.Common.getCurrentLanguage
import com.foodapp.app.utils.Common.getLog
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.ivBack
import kotlinx.android.synthetic.main.activity_cart.tvNoDataFound
import kotlinx.android.synthetic.main.row_cart.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CartActivity : BaseActivity() {
    var cartItemAdapter: BaseAdaptor<CartItemModel>? = null
    var cartItem: ArrayList<CartItemModel>? = ArrayList()
    var type=""
    override fun setLayout(): Int {
        return R.layout.activity_cart
    }

    override fun InitView() {
        getCurrentLanguage(this@CartActivity, false)
        tvCheckout.visibility = View.GONE
        if (isCheckNetwork(this@CartActivity)) {
            callApiCart(false)
        } else {
            alertErrorOrValidationDialog(
                this@CartActivity,
                resources.getString(R.string.no_internet)
            )
        }

        ivBack.setOnClickListener {
            finish()
        }

        if (getStringPref(
                this@CartActivity,
                SharePreference.SELECTED_LANGUAGE
            ).equals(resources.getString(R.string.language_hindi))
        ) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }

        ivHome.setOnClickListener {
            val intent =
                Intent(this@CartActivity, DashboardActivity::class.java).putExtra("pos", "1")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        tvCheckout.setOnClickListener {
            if (isCheckNetwork(this@CartActivity)) {
                callApiIsOpen()
            } else {
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }


    private fun callApiCart(isQty: Boolean) {
        if (!isQty) {
            showLoadingProgress(this@CartActivity)
        }
        val map = HashMap<String, String>()
        map.put("user_id", getStringPref(this@CartActivity, SharePreference.userId)!!)
        val call = ApiClient.getClient.getCartItem(map)
        call.enqueue(object : Callback<ListResponse<CartItemModel>> {
            override fun onResponse(
                call: Call<ListResponse<CartItemModel>>,
                response: Response<ListResponse<CartItemModel>>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: ListResponse<CartItemModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data?.size!! > 0) {
                            rvCartFood.visibility = View.VISIBLE
                            tvNoDataFound.visibility = View.GONE
                            tvCheckout.visibility = View.VISIBLE
                            cartItem = restResponce.data
                            setFoodCartAdaptor(cartItem!!)
                        } else {
                            rvCartFood.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                            tvCheckout.visibility = View.GONE
                        }
                    }else{
                        rvCartFood.visibility = View.GONE
                        tvNoDataFound.visibility = View.VISIBLE
                        tvCheckout.visibility = View.GONE
                    }
                } else {
                    dismissLoadingProgress()
                    rvCartFood.visibility = View.GONE
                    tvNoDataFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ListResponse<CartItemModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    @SuppressLint("ResourceType", "NewApi")
    private fun setFoodCartAdaptor(cartItemList: ArrayList<CartItemModel>) {
        cartItemAdapter = object : BaseAdaptor<CartItemModel>(this@CartActivity, cartItem!!) {
            @SuppressLint("SetTextI18n")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: CartItemModel,
                position: Int
            ) {
                holder!!.itemView.tvFoodName.text = cartItem!!.get(position).getItem_name()
                val price=cartItem!![position].getQty()?.toInt()!!*cartItem!![position].getPrice()!!.toDouble()

                holder.itemView.tvFoodPrice.text =
                    Common.getCurrancy(this@CartActivity) + String.format(
                        Locale.US,
                        "%,.2f",
                        price
                    )
                holder.itemView.tvFoodQty.text = cartItem!![position].getQty()
                holder.itemView.tvWeight.text=cartItem!![position].getVariation()

                Glide.with(this@CartActivity).load(cartItem!![position].getItemImage())
                    .placeholder(R.drawable.ic_placeholder).into(holder.itemView.ivFoodCart)

                if (cartItem!!.get(position).getAddons_id().equals("") || cartItem!!.get(position)
                        .getAddons_id() == null
                ) {
                    holder.itemView.tvAddons.visibility=View.GONE

                    holder.itemView.tvAddons.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.gray))
                } else {
                    holder.itemView.tvAddons.visibility=View.VISIBLE

                    holder.itemView.tvAddons.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                }
                if (cartItem!![position].getItem_notes() == null || cartItem!![position].getItem_notes() == "") {
                    holder.itemView.tvNotes.visibility=View.GONE

                    holder.itemView.tvNotes.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.gray))
                } else {
                    holder.itemView.tvNotes.visibility=View.VISIBLE

                    holder.itemView.tvNotes.backgroundTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                }

                val getIds = cartItem?.get(position)?.getAddons_id()?.split(",")?.toTypedArray()
                val getNames = cartItem?.get(position)?.getAddOnsName()?.split(",")?.toTypedArray()
                val getPrice = cartItem?.get(position)?.getAddOnsPrice()?.split(",")?.toTypedArray()

                val addOnList = ArrayList<AddonsModel>()
                for (i in 0 until getIds?.size!!) {
                    if (getIds[i] != "") {
                        addOnList.add(
                            AddonsModel(
                                getPrice?.get(i).toString(),
                                getNames?.get(i).toString(),
                                getIds.get(i).toString()
                            )
                        )

                    }
                }



                holder.itemView.tvAddons.setOnClickListener {
                    if (addOnList.size > 0) {
                        Common.openDialogSelectedAddons(this@CartActivity, addOnList)
                    }
                }

                holder.itemView.tvNotes.setOnClickListener {
                    if (!cartItem!![position].getItem_notes().isNullOrEmpty()) {
                        Common.alertNotesDialog(
                            this@CartActivity,
                            cartItem!!.get(position).getItem_notes()
                        )
                    }
                }

                holder.itemView.ivDeleteCartItem.setOnClickListener {
                    if (isCheckNetwork(this@CartActivity)) {
                        dlgDeleteConformationDialog(
                            this@CartActivity,
                            resources.getString(R.string.delete_cart_alert),
                            cartItem!!.get(position).getId()!!,
                            position
                        )
                    } else {
                        alertErrorOrValidationDialog(
                            this@CartActivity,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }

                holder.itemView.ivMinus.setOnClickListener {
                    if (cartItem!!.get(position).getQty()!!.toInt() > 1) {
                        holder.itemView.ivMinus.isClickable = true
                        type="decreaseValue"
                        getLog("Qty>>", cartItem!!.get(position).getQty().toString())
                        if (isCheckNetwork(this@CartActivity)) {
                            callApiCartQTYUpdate(cartItemList.get(position), type, false)
                        } else {
                            alertErrorOrValidationDialog(
                                this@CartActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else {
                        holder.itemView.ivMinus.isClickable = false
                        getLog("Qty1>>", cartItem!!.get(position).getQty().toString())
                    }
                }
                holder.itemView.ivPlus.setOnClickListener {
                    if (cartItem!!.get(position).getQty()!!.toInt() < getStringPref(
                            this@CartActivity,
                            SharePreference.isMiniMumQty
                        )!!.toInt()
                    ) {
                        type="increaseValue"
                        if (isCheckNetwork(this@CartActivity)) {

                            callApiCartQTYUpdate(cartItemList.get(position), type, true)
                        } else {
                            alertErrorOrValidationDialog(
                                this@CartActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else {
                        alertErrorOrValidationDialog(
                            this@CartActivity,
                            resources.getString(R.string.max_qty)
                        )
                    }
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_cart
            }

        }
        rvCartFood.adapter = cartItemAdapter
        rvCartFood.layoutManager = LinearLayoutManager(this@CartActivity)
        rvCartFood.itemAnimator = DefaultItemAnimator()
        rvCartFood.isNestedScrollingEnabled = true
    }

    private fun callApiCartQTYUpdate(
        cartModel: CartItemModel,
        type: String,
        isPlus: Boolean

    ) {

      val qty = if (isPlus) {
            cartModel.getQty()!!.toInt() + 1
        } else {
            cartModel.getQty()!!.toInt() - 1
        }
        showLoadingProgress(this@CartActivity)
        val map = HashMap<String, String>()
        map.put("cart_id", cartModel.getId()!!)
        map.put("item_id", cartModel.getItem_id()!!)
        map.put("qty", qty.toString())
        map.put("type", type.toString())
        map.put("user_id", getStringPref(this@CartActivity, SharePreference.userId)!!)

        val call = ApiClient.getClient.setQtyUpdate(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: SingleResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        callApiCart(true)
                    } else {
                        dismissLoadingProgress()
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiCartItemDelete(strCartId: String, pos: Int) {
        showLoadingProgress(this@CartActivity)
        val map = HashMap<String, String>()
        map.put("cart_id", strCartId)
        val call = ApiClient.getClient.setDeleteCartItem(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: SingleResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        Common.isCartTrue = true
                        Common.isCartTrueOut = true
                        cartItem!!.removeAt(pos)
                        cartItemAdapter!!.notifyDataSetChanged()
                        if (cartItem!!.size > 0) {
                            tvCheckout.visibility = View.VISIBLE
                        } else {
                            tvCheckout.visibility = View.GONE
                            rvCartFood.visibility = View.GONE
                            tvNoDataFound.visibility = View.VISIBLE
                            tvCheckout.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun dlgDeleteConformationDialog(act: Activity, msg: String?, strCartId: String, pos: Int) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
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
            val m_view = m_inflater.inflate(R.layout.dlg_confomation, null, false)
            val textDesc: TextView = m_view.findViewById(R.id.tvDesc)
            textDesc.text = msg
            val tvOk: TextView = m_view.findViewById(R.id.tvYes)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                if (isCheckNetwork(this@CartActivity)) {
                    finalDialog.dismiss()
                    callApiCartItemDelete(strCartId, pos)
                } else {
                    alertErrorOrValidationDialog(
                        this@CartActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            val tvCancle: TextView = m_view.findViewById(R.id.tvNo)
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(m_view)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@CartActivity, false)
    }

    private fun callApiIsOpen() {
        showLoadingProgress(this@CartActivity)
        val call = ApiClient.getClient.getCheckStatusRestaurant()
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: SingleResponse = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        startActivity(Intent(this@CartActivity, OrderSummuryActivity::class.java))
                    } else if (restResponce.getStatus()!! == "0") {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@CartActivity,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@CartActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}
