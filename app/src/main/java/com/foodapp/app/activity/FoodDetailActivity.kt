package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.adaptor.ImageSliderAdaptor
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.RestResponse
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.*
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.getCurrancy
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getBooleanPref
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import com.foodapp.app.utils.SharePreference.Companion.isCurrancy
import com.foodapp.app.utils.SharePreference.Companion.isLogin
import com.foodapp.app.utils.SharePreference.Companion.userId
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_foodorderdetail.*
import kotlinx.android.synthetic.main.activity_foodorderdetail.ivCart
import kotlinx.android.synthetic.main.activity_foodorderdetail.rlCount
import kotlinx.android.synthetic.main.activity_foodorderdetail.tvCount
import kotlinx.android.synthetic.main.row_ingrediants.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class FoodDetailActivity : BaseActivity() {
    var timer: Timer? = null
    private var currentPage = 0
    private var imagelist: ArrayList<String>? = null
    private var imagelistPlaceHolder: ArrayList<Drawable>? = null
    var itemModel: ItemDetailModel? = null
    private var listWeight: ArrayList<String>? = null
    private var listPrice: ArrayList<String>? = null
    private var strPrice: String = ""
    private var strProductPrice: String = ""
    private var strWeight: String = ""
    var VariationId = 0

    private var listProductPrice: ArrayList<String>? = null
    var listSeletecAddons = ArrayList<AddonsModel>()
    var qty = 0
    var itemId = ""
    var foodAdapter: BaseAdaptor<FoodItemModel>? = null
    val foodList = ArrayList<FoodItemModel>()
    var getActivity = ""
    var manager: LinearLayoutManager? = null
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisiblesItems = 0
    var isLoding = true
    var catId = ""
    override fun setLayout(): Int {
        return R.layout.activity_foodorderdetail
    }

    override fun InitView() {
        imagelist = ArrayList()
        imagelistPlaceHolder = ArrayList()
        listWeight = ArrayList()
        listPrice = ArrayList()
        listProductPrice = ArrayList()
        itemId = intent.getStringExtra("foodItemId")!!
        manager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        if (isCheckNetwork(this@FoodDetailActivity)) {
            callApiFoodDetail(intent.getStringExtra("foodItemId")!!)
        } else {
            alertErrorOrValidationDialog(
                this@FoodDetailActivity,
                resources.getString(R.string.no_internet)
            )
        }

        ivBack.setOnClickListener {
            finish()
        }

        if (getStringPref(this@FoodDetailActivity, SharePreference.SELECTED_LANGUAGE).equals(
                resources.getString(R.string.language_hindi)
            )
        ) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }
        ivCart.setOnClickListener {
            if (getBooleanPref(this@FoodDetailActivity, SharePreference.isLogin)) {
                openActivity(CartActivity::class.java)
            } else {
                openActivity(LoginActivity::class.java)
                finish()
                finishAffinity()
            }
        }

        rvItemRelatedProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dx > 0) {
                    visibleItemCount = manager!!.getChildCount()
                    totalItemCount = manager!!.getItemCount()
                    pastVisiblesItems = manager!!.findFirstVisibleItemPosition()
                    if (isLoding && CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            isLoding = false
                            callApiRelativeProduct(
                                true,
                                false,
                                itemId
                            )

                        }
                    }
                }
            }
        })
    }

    private fun loadPagerImages(imageHase: ArrayList<*>) {
        val adapter = ImageSliderAdaptor(this@FoodDetailActivity, imageHase)
        tabLayout.setupWithViewPager(viewPager, true)
        viewPager.setAdapter(adapter)
        val handler = Handler()

        if (imageHase.size == 1) {
            tabLayout.setVisibility(View.GONE)
        }
        val Update = Runnable {
            if (currentPage == imageHase.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 500, 3000)


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onPause() {
        super.onPause()
        if (timer != null)
            timer!!.cancel()
    }


    override fun onResume() {
        super.onResume()
        if (getBooleanPref(this@FoodDetailActivity, SharePreference.isLogin)) {
            rlCount.visibility = View.VISIBLE
            if (Common.isCartTrue) {
                if (isCheckNetwork(this@FoodDetailActivity)) {
                    Common.isCartTrue = false
                    callApiCartCount(false, false, "")
                } else {
                    alertErrorOrValidationDialog(
                        this@FoodDetailActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        } else {
            rlCount.visibility = View.GONE
            timer = Timer()
            val handler = Handler()
            val Update = Runnable {
                if (currentPage == imagelist!!.size) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
            }

            if (imagelist!!.size == 1) {
                tabLayout.visibility = View.GONE
            }
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    handler.post(Update)
                }
            }, 4000, 3000)
        }

    }


    private fun callApiFoodDetail(strFoodId: String) {
        showLoadingProgress(this@FoodDetailActivity)
        val map = HashMap<String, String>()
        map.put("item_id", strFoodId)
        val call = ApiClient.getClient.setItemDetail(map)
        call.enqueue(object : Callback<RestResponse<ItemDetailModel>> {
            override fun onResponse(
                call: Call<RestResponse<ItemDetailModel>>,
                response: Response<RestResponse<ItemDetailModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<ItemDetailModel> = response.body()!!
                    setRestaurantData(restResponce.getData())
                    callApiRelativeProduct(
                        false,
                        false,
                        strFoodId
                    )

                    if (getBooleanPref(
                            this@FoodDetailActivity,
                            SharePreference.isLogin
                        )
                    ) {
                        if (isCheckNetwork(this@FoodDetailActivity)) {
                            callApiCartCount(true, false, "")
                        } else {
                            alertErrorOrValidationDialog(
                                this@FoodDetailActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else {
                        dismissLoadingProgress()
                    }

                } else {
                    if (getBooleanPref(
                            this@FoodDetailActivity,
                            SharePreference.isLogin
                        )
                    ) {
                        if (isCheckNetwork(this@FoodDetailActivity)) {
                            callApiCartCount(true, false, "")
                        } else {
                            alertErrorOrValidationDialog(
                                this@FoodDetailActivity,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    } else {
                        dismissLoadingProgress()
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<ItemDetailModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiAddToCart(
        itemDetailModel: ItemDetailModel,
        strAddonsGetId: String,
        strAddonsGetPrice: String,
        strAddonsGetName: String,
        listAddons: ArrayList<AddonsModel>
    ) {
        showLoadingProgress(this@FoodDetailActivity)
        val price = tvAddtoCart.text.toString().replace(
            resources.getString(R.string.addtocart) + " " + getStringPref(
                this@FoodDetailActivity,
                isCurrancy
            ), ""
        )
        val actulePrice = price.replace(",", "")
        Common.getLog("getPrice", actulePrice)
        val map = HashMap<String, String>()
        map.put("item_id", itemDetailModel.getId()!!)
        map.put("addons_id", strAddonsGetId)
        map.put("item_notes", edNotes.text.toString())
        map.put("qty", tvFoodQty.text.toString())
        map.put("price", String.format(Locale.US, "%.02f", actulePrice.toDouble()))
        map.put("user_id", getStringPref(this@FoodDetailActivity, userId)!!)
        map.put("addons_price", strAddonsGetPrice)
        map.put("addons_name", strAddonsGetName)
        map.put("item_price", String.format(Locale.US, "%.02f", strPrice.toDouble()))
        map.put("item_image", itemDetailModel.getImages()?.get(0)?.getImageName().toString())
        map.put("item_name", itemDetailModel.getItem_name().toString())
        map.put("variation_id", VariationId.toString())
        map.put("variation_price", String.format(Locale.US, "%.02f", strPrice.toDouble()))
        map.put("variation", strWeight)
        map.put("tax", itemDetailModel.getItem_name().toString())

        val call = ApiClient.getClient.setAddToCart(map)

        Log.e("AddToCartRequest", Gson().toJson(map))
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                val restResponce: SingleResponse = response.body()!!
                if (isCheckNetwork(this@FoodDetailActivity)) {
                    callApiCartCount(true, true, restResponce.getMessage()!!)
                } else {
                    alertErrorOrValidationDialog(
                        this@FoodDetailActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
                if (response.code() == 200) {
                    if (restResponce.getStatus().equals("1")) {
                        val listAddos = ArrayList<AddonsModel>()
                        setSelectedAddonsAdaptor(listAddos)
                        edNotes.setText("")
                        qty = 0
                        tvFoodQty.text = "1"
                        tvAddtoCart.text =
                            resources.getString(R.string.addtocart) + " " + getStringPref(
                                this@FoodDetailActivity,
                                isCurrancy
                            ).plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    itemModel!!.getVariation()?.get(0)?.productPrice!!.toDouble()
                                )
                            )
                        listAddons.clear()
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    alertErrorOrValidationDialog(
                        this@FoodDetailActivity,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
            }
        })
    }

    @SuppressLint("SetTextI18n", "NewApi")
    private fun setRestaurantData(restResponce: ItemDetailModel?) {
        itemModel = restResponce
        if (restResponce!!.getImages()!!.size > 0) {
            val listImage = ArrayList<String>()
            for (i in 0 until restResponce.getImages()!!.size) {
                listImage.add(restResponce.getImages()!!.get(i).getItemimage()!!)
            }
            loadPagerImages(listImage)
        } else {
            imagelistPlaceHolder!!.add(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_placeholder,
                    null
                )!!
            )
            loadPagerImages(imagelistPlaceHolder!!)
        }


        listWeight!!.clear()
        listPrice!!.clear()
        listProductPrice?.clear()

        for (i in 0 until restResponce.getVariation()!!.size) {
            listWeight!!.add(restResponce.getVariation()!![i].variation!!)
            listPrice!!.add(restResponce.getVariation()!![i].productPrice!!)
            if (restResponce.getVariation()!![i].salePrice != 0) {
                listProductPrice!!.add(restResponce.getVariation()!![i].salePrice!!.toString())

            }
        }
        val adapter = ArrayAdapter(
            this@FoodDetailActivity,
            R.layout.textview_spinner,
            listWeight!!
        )
        adapter.setDropDownViewResource(R.layout.textview_spinner)
        spWeight.adapter = adapter

        for (i in 0 until restResponce.getVariation()!!.size) {
            tvVariation.text = restResponce.getVariation()!![i].variation!!
            strPrice = restResponce.getVariation()!![i].productPrice!!
            strProductPrice = restResponce.getVariation()!![i].salePrice.toString()
            strWeight = restResponce.getVariation()!![i].variation!!
            spWeight.setSelection(i)
            break
        }

        spWeight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                tvVariation.text = listWeight!![position]
                strPrice = listPrice!![position]
                if (listProductPrice?.size ?: 0 > 0) {
                    strProductPrice = listProductPrice?.get(position) ?: ""

                }
                Log.e("posittion", position.toString())
                Log.e("strWeight", strWeight)
                Log.e("listWeight", listWeight!![position])
                strWeight = listWeight!![position]
                VariationId = restResponce.getVariation()!![position].id!!
                tvFoodWeightType.text = strWeight

                tvFoodQty.text = "1"
                tvFoodPrice.text = getCurrancy(this@FoodDetailActivity) + String.format(
                    Locale.US,
                    "%,.2f",
                    strPrice.toDouble()
                )
                if (strProductPrice != "null" && strProductPrice.isNotEmpty()) {
                    tvSalePrice.text = getCurrancy(this@FoodDetailActivity) + String.format(
                        Locale.US,
                        "%,.2f",
                        strProductPrice.toDouble()
                    )
                } else {
                    tvSalePrice.text = ""
                }
                /*    qty=1
                val getPrice = qty * strPrice.toDouble()*/
                tvAddtoCart.text =
                    resources.getString(R.string.addtocart) + " " + getCurrancy(this@FoodDetailActivity).plus(
                        String.format(Locale.US, "%,.2f", strPrice.toDouble())
                    )
            }
        }

        when {
            restResponce.getVariation()?.size ?: 0 > 1 -> {
                cvSelectVariation.visibility = View.VISIBLE
            }
            restResponce.getVariation()?.size ?: 0 == 1 -> {
                tvFoodWeightType.text=restResponce.getVariation()?.get(0)?.variation.toString()
                cvSelectVariation.visibility = View.GONE

            }
            else -> {
                cvSelectVariation.visibility = View.GONE

            }
        }


        rlPrice.setOnClickListener {
            spWeight.performClick()
        }


        if (restResponce.getIngredients()!!.size > 0) {
            rvIngredients.visibility = View.VISIBLE
            tvNoDataFound.visibility = View.GONE
            linearIngrediants.visibility = View.VISIBLE
            tvIngredientsTitle.visibility = View.VISIBLE
            setItemIngrdiantsAdaptor(restResponce.getIngredients()!!)
        } else {
            rvIngredients.visibility = View.GONE
            tvNoDataFound.visibility = View.VISIBLE
            linearIngrediants.visibility = View.GONE
            tvIngredientsTitle.visibility = View.GONE
        }





        tvFoodName.text = restResponce.getItem_name()
        tvFoodPrice.text = getStringPref(this@FoodDetailActivity, isCurrancy) + String.format(
            Locale.US,
            "%,.2f",
            restResponce.getVariation()?.get(0)?.productPrice!!.toDouble()
        )

        /*  if (restResponce.getTax() ?: 0 > 0) {
              tvTaxesNote.text = "+ ".plus(restResponce.getTax()).plus("%").plus(" Additional Tax")
              tvTaxesNote.setTextColor(Color.RED)
          }*/

        if (!restResponce.getTax().isNullOrEmpty() && restResponce.getTax() != "0") {
            tvTaxesNote.text = "+ ".plus(restResponce.getTax()).plus("%").plus(" Additional Tax")
            tvTaxesNote.setTextColor(Color.RED)
        }

        tvFoodType.text = restResponce.getCategory_name()
        tvTime.text = resources.getString(R.string.estimated_time).plus(" ")
            .plus(restResponce.getDelivery_time())
        tvDetail.text = restResponce.getItem_description()

        tvAddtoCart.text = resources.getString(R.string.addtocart) + " ${
            getStringPref(
                this@FoodDetailActivity,
                isCurrancy
            ).plus(
                String.format(
                    Locale.US,
                    "%,.2f",
                    itemModel!!.getVariation()?.get(0)?.productPrice!!.toDouble()
                )
            )
        }"

        if (itemModel!!.getVariation()?.get(0)?.salePrice != 0) {
            tvSalePrice.text = getStringPref(this@FoodDetailActivity, isCurrancy) + String.format(
                Locale.US,
                "%,.2f",
                restResponce.getVariation()?.get(0)?.salePrice!!.toDouble()
            )
        } else {
            tvSalePrice.visibility = View.GONE
        }

        tvSalePrice.paintFlags =
            tvSalePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        qty = tvFoodQty.text.toString().toInt()
        ivMinus.setOnClickListener {
            if (qty > 1) {
                qty = qty - 1
                tvFoodQty.text = qty.toString()
                if (listSeletecAddons.size > 0) {
                    var price: Double = 0.0
                    for (i in 0 until listSeletecAddons.size) {
                        price += listSeletecAddons[i].getPrice()!!.toDouble()
                    }
                    val getPrice = qty * (price + itemModel!!.getVariation()
                        ?.get(0)?.productPrice!!.toDouble())
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart) + " " + getStringPref(
                            this@FoodDetailActivity,
                            isCurrancy
                        ).plus(
                            String.format(Locale.US, "%,.2f", getPrice)
                        )
                } else {
                    val getPrice =
                        qty * itemModel!!.getVariation()?.get(0)?.productPrice!!.toDouble()
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart) + " " + getStringPref(
                            this@FoodDetailActivity,
                            isCurrancy
                        ).plus(
                            String.format(Locale.US, "%,.2f", getPrice)
                        )
                }
            }
        }

        ivPlus.setOnClickListener {
            if (qty < getStringPref(
                    this@FoodDetailActivity,
                    SharePreference.isMiniMumQty
                )!!.toInt()
            ) {
                qty = qty + 1
                tvFoodQty.text = qty.toString()
                if (listSeletecAddons.size > 0) {
                    var price: Double = 0.0
                    for (i in 0 until listSeletecAddons.size) {
                        price += listSeletecAddons[i].getPrice()!!.toDouble()
                    }
                    val getPrice = qty * (price + itemModel!!.getVariation()
                        ?.get(0)?.productPrice!!.toDouble())
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart) + " " + getStringPref(
                            this@FoodDetailActivity,
                            isCurrancy
                        ).plus(String.format(Locale.US, "%,.2f", getPrice))
                } else {
                    val getPrice =
                        qty * itemModel!!.getVariation()?.get(0)?.productPrice!!.toDouble()
                    tvAddtoCart.text =
                        resources.getString(R.string.addtocart) + " " + getStringPref(
                            this@FoodDetailActivity,
                            isCurrancy
                        ).plus(String.format(Locale.US, "%,.2f", getPrice))
                }
            } else {
                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.max_qty)
                )
            }

        }

        if (itemModel!!.getItem_status().equals("2")) {
            tvAddtoCart.visibility = View.GONE
            rlItem.visibility = View.VISIBLE
            rlQty.visibility = View.GONE
        } else {
            tvAddtoCart.visibility = View.VISIBLE
            rlItem.visibility = View.GONE
            rlQty.visibility = View.GONE
        }

        if (restResponce.getAddons().size > 0) {
            ivAddAdons?.visibility = View.VISIBLE
            tvNoDataAddonsFound?.text = resources.getString(R.string.select_add_ons)
            relAddOns.visibility = View.VISIBLE
            rlTitleAddons.visibility = View.VISIBLE
            ivAddAdons.imageTintList = ColorStateList.valueOf(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorPrimary,
                    null
                )
            )
        } else {
            ivAddAdons.imageTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray, null))
            ivAddAdons?.visibility = View.GONE
            tvNoDataAddonsFound?.text = resources.getString(R.string.no_data_found)
            relAddOns.visibility = View.GONE
            rlTitleAddons.visibility = View.GONE

        }


        var listAddons = ArrayList<AddonsModel>()
        ivAddAdons.setOnClickListener {
            if (restResponce.getAddons().size > 0) {
                val list = restResponce.getAddons()
                for (i in 0 until list.size) {
                    list[i].setSelectAddons(false)
                }
                listAddons = openDialogAddons(this@FoodDetailActivity, list)
            }
        }

        tvAddtoCart.setOnClickListener {
            if (getBooleanPref(this@FoodDetailActivity, SharePreference.isLogin)) {
                var strAddonsGetId = ""
                var strAddonsGetPrice = ""
                var strAddonsGetName = ""
                Common.getLog("getFinalAddonsList", Gson().toJson(listAddons))
                if (listAddons.size > 0) {
                    if (listAddons.size == 1) {
                        strAddonsGetId = listAddons[0].getId()!!
                        strAddonsGetPrice =
                            String.format(Locale.US, "%.02f", listAddons[0].getPrice()?.toDouble())
                        strAddonsGetName = listAddons[0].getName()!!
                    } else if (listAddons.size == 2) {
                        strAddonsGetId = listAddons[0].getId() + "," + listAddons[1].getId()
                        strAddonsGetPrice = String.format(
                            Locale.US,
                            "%.02f",
                            listAddons[0].getPrice()?.toDouble()
                        ) + "," + String.format(
                            Locale.US,
                            "%.02f",
                            listAddons[1].getPrice()?.toDouble()
                        )
                        strAddonsGetName = listAddons[0].getName() + "," + listAddons[1].getName()

                    } else if (listAddons.size > 2) {
                        val get = listAddons.size - 1
                        Common.getLog("getFinalListId", get.toString())
                        for (i in 0 until listAddons.size) {
                            if (i == 0) {
                                strAddonsGetId = listAddons.get(i).getId()!! + ","
                                strAddonsGetPrice = String.format(
                                    Locale.US,
                                    "%.02f",
                                    listAddons.get(i).getPrice()?.toDouble()
                                ) + ","
                                strAddonsGetName = listAddons.get(i).getName()!! + ","
                            } else if (i == get) {
                                strAddonsGetId += listAddons.get(i).getId()
                                strAddonsGetPrice += listAddons.get(i).getPrice()
                                strAddonsGetName += listAddons.get(i).getName()
                            } else {
                                strAddonsGetId = strAddonsGetId + listAddons.get(i).getId() + ","
                                strAddonsGetPrice = strAddonsGetPrice + String.format(
                                    Locale.US,
                                    "%.02f",
                                    listAddons[i].getPrice()?.toDouble()
                                ) + ","
//                                strAddonsGetPrice = strAddonsGetPrice + listAddons.get(i).getPrice()?.toDouble().toString() + ","
                                strAddonsGetName =
                                    strAddonsGetName + listAddons.get(i).getName() + ","
                            }
                        }
                    }
                }

                if (isCheckNetwork(this@FoodDetailActivity)) {
                    callApiAddToCart(
                        itemModel!!,
                        strAddonsGetId,
                        strAddonsGetPrice,
                        strAddonsGetName,
                        listAddons
                    )

                } else {
                    alertErrorOrValidationDialog(
                        this@FoodDetailActivity,
                        resources.getString(R.string.no_internet)
                    )
                }
            } else {
                openActivity(LoginActivity::class.java)
                finish()
            }

        }
    }

    private fun setItemIngrdiantsAdaptor(ingredientsItemList: ArrayList<IngredientsModel>) {
        val cartItemAdapter =
            object : BaseAdaptor<IngredientsModel>(this@FoodDetailActivity, ingredientsItemList) {
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: IngredientsModel,
                    position: Int
                ) {
                    Glide.with(this@FoodDetailActivity)
                        .load(ingredientsItemList.get(position).getIngredients_image())
                        .into(holder!!.itemView.ivIngrediants)
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_ingrediants
                }

            }
        rvIngredients.adapter = cartItemAdapter
        rvIngredients.layoutManager =
            LinearLayoutManager(this@FoodDetailActivity, LinearLayoutManager.HORIZONTAL, false)
        rvIngredients.itemAnimator = DefaultItemAnimator()
        rvIngredients.isNestedScrollingEnabled = true
    }


    @SuppressLint("SetTextI18n")
    fun openDialogAddons(
        activity: Activity,
        addonsList: ArrayList<AddonsModel>
    ): ArrayList<AddonsModel> {
        val dialog: Dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val lp = WindowManager.LayoutParams()
        lp.windowAnimations = R.style.DialogAnimation
        dialog.window!!.attributes = lp
        dialog.setContentView(R.layout.dlg_addons)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ivCancel = dialog.findViewById<ImageView>(R.id.ivCancel)
        val rvAddons = dialog.findViewById<RecyclerView>(R.id.rvAddons)
        val tvDone = dialog.findViewById<TextView>(R.id.tvDone)
        val addonsSeletedList = setAddonsAdaptor(rvAddons, addonsList)
        ivCancel.setOnClickListener {
            dialog.dismiss()
        }
        val addonsSelectedList = ArrayList<AddonsModel>()
        tvDone.setOnClickListener {
            addonsSelectedList.clear()
            for (i in 0 until addonsSeletedList.size) {
                if (addonsSeletedList[i].isSelectAddons()!!) {
                    val addonsModel = addonsSeletedList[i]
                    addonsSelectedList.add(addonsModel)
                }
            }
            listSeletecAddons = setSelectedAddonsAdaptor(addonsSelectedList)
            var price = 0.0
            for (i in 0 until listSeletecAddons.size) {
                price += listSeletecAddons.get(i).getPrice()!!.toDouble()
            }
            val getPrice =
                tvFoodQty.text.toString().toInt() * (price + itemModel!!.getVariation()
                    ?.get(0)?.productPrice!!.toDouble()!!
                    .toDouble())
            tvAddtoCart.text =
                resources.getString(R.string.addtocart) + " " + getStringPref(
                    this@FoodDetailActivity,
                    isCurrancy
                ).plus(String.format(Locale.US, "%,.2f", getPrice))
            dialog.dismiss()
        }
        dialog.show()
        return addonsSelectedList
    }

    private fun setAddonsAdaptor(
        rvAddons: RecyclerView,
        addonsList: ArrayList<AddonsModel>
    ): ArrayList<AddonsModel> {
        var getAddonsList = addonsList
        Common.getLog("getAddonsData", "===" + Gson().toJson(getAddonsList).toString() + "")
        val addonsItemAdapter =
            object : BaseAdaptor<AddonsModel>(this@FoodDetailActivity, getAddonsList) {
                @SuppressLint("NewApi", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: AddonsModel,
                    position: Int
                ) {
                    val tvAddonsName: TextView = holder!!.itemView.findViewById(R.id.tvAddonsName)
                    val tvAddonsPrice: TextView = holder.itemView.findViewById(R.id.tvAddonsPrice)
                    val ivCheck: ImageView = holder.itemView.findViewById(R.id.ivCheck)
                    tvAddonsName.text = getAddonsList[position].getName()
                    if (String.format(
                            Locale.US,
                            "%.2f",
                            getAddonsList[position].getPrice()!!.toDouble()
                        ) == "0.00"
                    ) {
                        tvAddonsPrice.text = resources.getString(R.string.free)
                    } else {
                        tvAddonsPrice.text = getStringPref(
                            this@FoodDetailActivity,
                            isCurrancy
                        ) + String.format(
                            Locale.US,
                            "%,.2f",
                            getAddonsList[position].getPrice()!!.toDouble()
                        )
                    }

                    if (getAddonsList[position].isSelectAddons()!!) {
                        ivCheck.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_check,
                                null
                            )
                        )
                        ivCheck.imageTintList =
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.colorPrimary,
                                    null
                                )
                            )
                    } else {
                        ivCheck.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_uncheck,
                                null
                            )
                        )
                        ivCheck.imageTintList =
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    resources,
                                    R.color.colorPrimary,
                                    null
                                )
                            )
                    }

                    ivCheck.setOnClickListener {
                        if (getAddonsList[position].isSelectAddons()!!) {
                            ivCheck.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_uncheck,
                                    null
                                )
                            )
                            ivCheck.imageTintList =
                                ColorStateList.valueOf(
                                    ResourcesCompat.getColor(
                                        resources,
                                        R.color.colorPrimary,
                                        null
                                    )
                                )
                            getAddonsList[position].setSelectAddons(false)
                            notifyDataSetChanged()
                        } else {
                            ivCheck.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_check,
                                    null
                                )
                            )
                            ivCheck.imageTintList =
                                ColorStateList.valueOf(
                                    ResourcesCompat.getColor(
                                        resources,
                                        R.color.colorPrimary,
                                        null
                                    )
                                )
                            getAddonsList[position].setSelectAddons(true)
                            notifyDataSetChanged()
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_addons
                }

            }
        rvAddons.adapter = addonsItemAdapter
        rvAddons.layoutManager = LinearLayoutManager(this@FoodDetailActivity)
        rvAddons.itemAnimator = DefaultItemAnimator()
        rvAddons.isNestedScrollingEnabled = true
        return getAddonsList
    }

    private fun setSelectedAddonsAdaptor(lisAddons: ArrayList<AddonsModel>): ArrayList<AddonsModel> {
        val getSelectedlist = lisAddons
        val selectedaddonsItemAdapter =
            object : BaseAdaptor<AddonsModel>(this@FoodDetailActivity, getSelectedlist) {
                @SuppressLint("SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: AddonsModel,
                    position: Int
                ) {
                    val tvAddonsName: TextView = holder!!.itemView.findViewById(R.id.tvAddonsName)
                    val tvAddonsPrice: TextView = holder.itemView.findViewById(R.id.tvAddonsPrice)
                    val ivCancel: ImageView = holder.itemView.findViewById(R.id.ivCancel)
                    tvAddonsName.text = lisAddons[position].getName()
                    if (String.format(
                            Locale.US,
                            "%,.2f",
                            lisAddons[position].getPrice()!!.toDouble()
                        ) == "0.00"
                    ) {
                        tvAddonsPrice.text = resources.getString(R.string.free)
                    } else {
                        tvAddonsPrice.text = getStringPref(
                            this@FoodDetailActivity,
                            isCurrancy
                        ) + String.format(
                            Locale.US,
                            "%,.2f",
                            lisAddons[position].getPrice()!!.toDouble()
                        )
                    }

                    ivCancel.setOnClickListener {
                        lisAddons.removeAt(position)
                        if (lisAddons.size > 0) {
                            rvAddons.visibility = View.VISIBLE
                            tvNoDataAddonsFound.visibility = View.GONE
                            relAddOns.visibility = View.VISIBLE
                            var price = 0.0
                            for (i in 0 until getSelectedlist.size) {
                                price = price + getSelectedlist.get(i).getPrice()!!.toDouble()
                            }
                            val getPrice: Double = tvFoodQty.text.toString()
                                .toInt() * (price + itemModel!!.getVariation()
                                ?.get(0)?.productPrice!!.toDouble())
                            tvAddtoCart.text =
                                resources.getString(R.string.addtocart) + " " + getStringPref(
                                    this@FoodDetailActivity,
                                    isCurrancy
                                ).plus(String.format(Locale.US, "%,.2f", getPrice))
                        } else {
                            rvAddons.visibility = View.GONE
                            tvNoDataAddonsFound.visibility = View.VISIBLE
                            val getPrice: Double =
                                tvFoodQty.text.toString().toInt() * itemModel!!.getVariation()
                                    ?.get(0)?.productPrice!!.toDouble()!!
                                    .toDouble()
                            tvAddtoCart.text =
                                resources.getString(R.string.addtocart) + " " + getStringPref(
                                    this@FoodDetailActivity,
                                    isCurrancy
                                ).plus(String.format(Locale.US, "%,.2f", getPrice))

                        }
                        notifyDataSetChanged()
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_selectedaddons
                }

            }

        if (lisAddons.size > 0) {
            rvAddons.visibility = View.VISIBLE
//            relAddOns.visibility = View.VISIBLE
            tvNoDataAddonsFound.visibility = View.GONE
            rvAddons.adapter = selectedaddonsItemAdapter
            rvAddons.layoutManager = LinearLayoutManager(this@FoodDetailActivity)
            rvAddons.itemAnimator = DefaultItemAnimator()
            rvAddons.isNestedScrollingEnabled = true
        } else {
            rvAddons.visibility = View.GONE
            tvNoDataAddonsFound.visibility = View.VISIBLE
//            relAddOns.visibility = View.GONE
        }

        return getSelectedlist

    }

    private fun callApiCartCount(isFristTime: Boolean, isQtyUpdate: Boolean, strMsg: String) {
        if (!isFristTime) {
            showLoadingProgress(this@FoodDetailActivity)
        }
        val map = HashMap<String, String>()
        map["user_id"] = getStringPref(this@FoodDetailActivity, userId)!!
        val call = ApiClient.getClient.getCartCount(map)
        call.enqueue(object : Callback<CartCountModel> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<CartCountModel>,
                response: Response<CartCountModel>
            ) {
                if (response.code() == 200) {
                    val restResponce: CartCountModel = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        tvCount.text = restResponce.getCart()
                        if (isQtyUpdate) {
                            Common.isCartTrueOut = true
                            dismissLoadingProgress()
                        } else {
                            dismissLoadingProgress()
                        }

                        if (!isFristTime) {
                            timer = Timer()
                            val handler = Handler()
                            val Update = Runnable {
                                if (currentPage == imagelist!!.size) {
                                    currentPage = 0
                                }
                                viewPager.setCurrentItem(currentPage++, true)
                            }

                            if (imagelist!!.size == 1) {
                                tabLayout.visibility = View.GONE
                            }
                            timer!!.schedule(object : TimerTask() {
                                override fun run() {
                                    handler.post(Update)
                                }
                            }, 4000, 3000)
                        }
                    } else if (restResponce.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        tvCount!!.text = "0"
                        if (isQtyUpdate) {
                            alertErrorOrValidationDialog(
                                this@FoodDetailActivity,
                                strMsg
                            )
                        }
                        if (!isFristTime) {
                            timer = Timer()
                            val handler = Handler(Looper.getMainLooper())
                            val Update = Runnable {
                                if (currentPage == imagelist!!.size) {
                                    currentPage = 0
                                }
                                viewPager.setCurrentItem(currentPage++, true)
                            }

                            if (imagelist!!.size == 1) {
                                tabLayout.visibility = View.GONE
                            }
                            timer!!.schedule(object : TimerTask() {
                                override fun run() {
                                    handler.post(Update)
                                }
                            }, 4000, 3000)
                        }
                    }

                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@FoodDetailActivity,
                        error.getString("message")
                    )
                    if (!isFristTime) {
                        timer = Timer()
                        val handler = Handler()
                        val Update = Runnable {
                            if (currentPage == imagelist!!.size) {
                                currentPage = 0
                            }
                            viewPager.setCurrentItem(currentPage++, true)
                        }

                        if (imagelist!!.size == 1) {
                            tabLayout.visibility = View.GONE
                        }
                        timer!!.schedule(object : TimerTask() {
                            override fun run() {
                                handler.post(Update)
                            }
                        }, 4000, 3000)
                    }
                }
            }

            override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    private fun callApiRelativeProduct(
        isPageLoad: Boolean,
        isAddtoCart: Boolean,
        strFoodId: String
    ) {
        val map = HashMap<String, String>()
        map["item_id"] = strFoodId
        if (getBooleanPref(this@FoodDetailActivity, isLogin)) {
            map["user_id"] = getStringPref(this@FoodDetailActivity, userId)!!
        } else {
            map["user_id"] = ""
        }
        if (isAddtoCart) {
            showLoadingProgress(this@FoodDetailActivity)
        }
        if (isPageLoad) {
            llProgressbar.visibility = View.VISIBLE
        } else {
            llProgressbar.visibility = View.GONE
        }
        val call = ApiClient.getClient.getRelatedProduct(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<FoodItemResponseModel>> {
            override fun onResponse(
                call: Call<RestResponse<FoodItemResponseModel>>,
                response: Response<RestResponse<FoodItemResponseModel>>
            ) {
                if (response.code() == 200) {
                    val restResponse: RestResponse<FoodItemResponseModel> = response.body()!!
                    if (restResponse.getStatus() == "1") {
                        llProgressbar.visibility = View.GONE
                        if (!isPageLoad) {
                            foodList.clear()
                        }
                        TOTAL_PAGES = restResponse.getData()!!.lastPage!!.toInt()
                        val paginationModel: FoodItemResponseModel = restResponse.getData()!!
                        if (paginationModel.data!!.isNotEmpty()) {
                            tvItem.visibility = View.GONE
                            rvItemRelatedProduct.visibility = View.VISIBLE
                            relatedProduct.visibility = View.VISIBLE
                            foodList.addAll(paginationModel.data)
                            setFoodAdaptor(foodList)

                        } else {
                            tvItem.visibility = View.VISIBLE
                            relatedProduct.visibility = View.GONE

                            rvItemRelatedProduct.visibility = View.GONE
                        }


                        if (getBooleanPref(this@FoodDetailActivity, isLogin)) {
                            if (isCheckNetwork(this@FoodDetailActivity)) {
                                callApiCartCount(
                                    isFristTime = true,
                                    isQtyUpdate = false,
                                    strMsg = ""
                                )
                            } else {
                                alertErrorOrValidationDialog(
                                    this@FoodDetailActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }

                        } else {
                            dismissLoadingProgress()
                            rlCount!!.visibility = View.GONE
                        }

                    } else if (restResponse.getStatus() == "0") {
                        if (getBooleanPref(this@FoodDetailActivity, isLogin)) {
                            if (isCheckNetwork(this@FoodDetailActivity)) {
                                callApiCartCount(
                                    isFristTime = true,
                                    isQtyUpdate = false,
                                    strMsg = ""
                                )
                            } else {
                                alertErrorOrValidationDialog(
                                    this@FoodDetailActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        } else {
                            dismissLoadingProgress()
                            rlCount!!.visibility = View.GONE
                            alertErrorOrValidationDialog(
                                this@FoodDetailActivity,
                                restResponse.getMessage().toString()
                            )
                        }
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status") == "2") {
                        dismissLoadingProgress()
                        Common.setLogout(this@FoodDetailActivity)
                    } else {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@FoodDetailActivity,
                            error.getString("message")
                        )
                    }
                }

            }

            override fun onFailure(call: Call<RestResponse<FoodItemResponseModel>>, t: Throwable) {
                dismissLoadingProgress()
                Log.e("Error Message", t.message.toString())

                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodAdaptor(foodList: ArrayList<FoodItemModel>) {
        foodAdapter = object : BaseAdaptor<FoodItemModel>(this@FoodDetailActivity, foodList) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: FoodItemModel,
                position: Int
            ) {
                val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                val tvFoodOnSale: TextView = holder.itemView.findViewById(R.id.tvFoodOnSale)
                if(foodList[position].variation?.size?:0>0) {


                    if (foodList[position].variation?.get(0)?.salePrice != 0) {
                        tvFoodOnSale.visibility = View.VISIBLE
                    } else {
                        tvFoodOnSale.visibility = View.GONE

                    }
                    Common.getPrice(
                        foodList[position].variation?.get(0)?.productPrice!!.toDouble(),
                        tvFoodPriceGrid,
                        this@FoodDetailActivity
                    )
                }
                tvFoodName.text = foodList[position].itemName

                if (getBooleanPref(
                        this@FoodDetailActivity,
                        SharePreference.isLinearLayoutManager
                    )
                ) {
                    Glide.with(this@FoodDetailActivity)
                        .load(foodList[position].itemimage?.image).centerCrop()
                        .placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_placeholder,
                                null
                            )
                        )
                        .into(ivFood)
                } else {
                    Glide.with(this@FoodDetailActivity)
                        .load(foodList[position].itemimage!!.image).centerCrop()
                        .optionalFitCenter()
                        .placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_placeholder,
                                null
                            )
                        )
                        .into(ivFood)
                }

                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(
                            this@FoodDetailActivity,
                            FoodDetailActivity::class.java
                        ).putExtra("foodItemId", foodList[position].id)
                    )
                }




                tvFoodPriceGrid.visibility = View.VISIBLE

                if (foodList!!.get(position).isFavorite.equals("0")) {
                    icLike.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_unlike,
                            null
                        )
                    )
                    icLike.imageTintList = ColorStateList.valueOf(Color.WHITE)
                } else {
                    icLike.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_favourite_like,
                            null
                        )
                    )
                    icLike.imageTintList = ColorStateList.valueOf(Color.WHITE)
                }
                icLike.setOnClickListener {
                    if (SharePreference.getBooleanPref(
                            this@FoodDetailActivity,
                            SharePreference.isLogin
                        )
                    ) {
                        if (foodList!!.get(position).isFavorite.equals("0")) {
                            val map = HashMap<String, String>()
                            map["item_id"] = foodList!!.get(position).id!!
                            map["user_id"] = getStringPref(
                                this@FoodDetailActivity,
                                userId
                            )!!
                            if (isCheckNetwork(this@FoodDetailActivity)) {
                                callApiFavourite(map, position)
                            } else {
                                alertErrorOrValidationDialog(
                                    this@FoodDetailActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    } else {
                        openActivity(LoginActivity::class.java)
                        finish()
                    }

                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_food_related_product
            }

        }
        rvItemRelatedProduct?.adapter = foodAdapter
        rvItemRelatedProduct?.layoutManager = manager
        rvItemRelatedProduct?.isNestedScrollingEnabled = true
        rvItemRelatedProduct?.itemAnimator = DefaultItemAnimator()

    }

    private fun callApiFavourite(hasmap: HashMap<String, String>, pos: Int) {
        Common.showLoadingProgress(this@FoodDetailActivity)
        val call = ApiClient.getClient.setAddFavorite(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        foodList[pos].isFavorite = "1"
                        foodAdapter!!.notifyDataSetChanged()
                    } else if (restResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@FoodDetailActivity,
                            restResponse.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@FoodDetailActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    var dialog: Dialog? = null

    fun dismissLoadingProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    fun showLoadingProgress(context: Activity) {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dlg_progress)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

}