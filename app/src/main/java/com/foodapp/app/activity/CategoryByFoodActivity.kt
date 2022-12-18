package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.RestResponse
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseActivity
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.model.FoodItemModel
import com.foodapp.app.model.FoodItemResponseModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.getStringPref
import kotlinx.android.synthetic.main.activity_categorybyproduct.*
import kotlinx.android.synthetic.main.activity_categorybyproduct.ivBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CategoryByFoodActivity : BaseActivity() {
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var foodAdapter: BaseAdaptor<FoodItemModel>? = null
    var foodList: ArrayList<FoodItemModel>? = null
    var manager1: GridLayoutManager? = null
    var rvFoodData: RecyclerView? = null
    var tvNoDataFound: TextView? = null
    override fun setLayout(): Int = R.layout.activity_categorybyproduct

    override fun InitView() {
        Common.getCurrentLanguage(this@CategoryByFoodActivity, false)
        tvCategoryName.text = intent.getStringExtra("CategoryName")
        foodList = ArrayList()
        rvFoodData = findViewById(R.id.rvFoodData)
        tvNoDataFound = findViewById(R.id.tvNoDataFound)
        manager1 = GridLayoutManager(this@CategoryByFoodActivity, 2, GridLayoutManager.VERTICAL, false)
        rvFoodData!!.layoutManager = manager1
        ivBack.setOnClickListener {
            finish()
        }
        if (Common.isCheckNetwork(this@CategoryByFoodActivity)) {
            callApiCategoryByFood(true)
        } else {
            Common.alertErrorOrValidationDialog(this@CategoryByFoodActivity, resources.getString(R.string.no_internet))
        }

        if (getStringPref(this@CategoryByFoodActivity, SharePreference.SELECTED_LANGUAGE).equals(resources.getString(R.string.language_hindi))) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }


        var visibleItemCount: Int = 0
        var totalItemCount: Int = 0
        var pastVisiblesItems: Int = 0

        rvFoodData!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager1!!.childCount
                    totalItemCount = manager1!!.itemCount
                    pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                    if (CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            if (Common.isCheckNetwork(this@CategoryByFoodActivity)) {
                                callApiCategoryByFood(false)
                            } else {
                                Common.alertErrorOrValidationDialog(this@CategoryByFoodActivity, resources.getString(R.string.no_internet))
                            }
                        }
                    }
                }
            }
        })
    }


    private fun callApiCategoryByFood(isFirstTime: Boolean?) {
        Common.showLoadingProgress(this@CategoryByFoodActivity)
        val map = HashMap<String, String>()
        map["cat_id"] = intent.getStringExtra("CategoryId")!!
        if (SharePreference.getBooleanPref(this@CategoryByFoodActivity, SharePreference.isLogin)) {
            map["user_id"] = getStringPref(this@CategoryByFoodActivity, SharePreference.userId)!!
        }
        val call = ApiClient.getClient.getFoodItem(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<FoodItemResponseModel>> {
            override fun onResponse(
                    call: Call<RestResponse<FoodItemResponseModel>>,
                    response: Response<RestResponse<FoodItemResponseModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<FoodItemResponseModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        val foodItemResponseModel: FoodItemResponseModel = restResponce.getData()!!
                        CURRENT_PAGES = foodItemResponseModel.currentPage!!.toInt()
                        TOTAL_PAGES = foodItemResponseModel.lastPage!!.toInt()
                        if (foodItemResponseModel.data!!.size > 0) {
                            rvFoodData!!.visibility = View.VISIBLE
                            tvNoDataFound!!.visibility = View.GONE
                            foodList!!.addAll(foodItemResponseModel.data)
                            setFoodAdaptor(isFirstTime!!)
                        } else {
                            rvFoodData!!.visibility = View.GONE
                            tvNoDataFound!!.visibility = View.VISIBLE
                        }

                    } else if (restResponce.getMessage().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                                this@CategoryByFoodActivity,
                                restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<FoodItemResponseModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@CategoryByFoodActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodAdaptor(isFirstTime: Boolean) {
        if (isFirstTime) {
            foodAdapter = object : BaseAdaptor<FoodItemModel>(this@CategoryByFoodActivity, foodList!!) {
                @SuppressLint("NewApi")
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
                    if (foodList!![position].variation?.get(0)?.salePrice != 0) {
                        tvFoodOnSale.visibility = View.VISIBLE
                    } else {
                        tvFoodOnSale.visibility = View.GONE

                    }
                    tvFoodName.text = foodList!![position].itemName
                    tvFoodPriceGrid.text = Common.getCurrancy(this@CategoryByFoodActivity).plus(String.format(Locale.US, "%,.2f",
                            foodList!![position].variation?.get(0)?.productPrice!!.toDouble()))
                    Glide.with(this@CategoryByFoodActivity).load(foodList!![position].itemimage!!.image).placeholder(ResourcesCompat.getDrawable(resources, R.drawable.ic_placeholder, null)).into(ivFood)
                    holder.itemView.setOnClickListener {
                        startActivity(Intent(this@CategoryByFoodActivity, FoodDetailActivity::class.java).putExtra("foodItemId",
                                foodList!![position].id))
                    }
                    tvFoodPriceGrid.visibility = View.VISIBLE

                    if (foodList!![position].isFavorite.equals("0")) {
                        icLike.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_unlike, null))
                        icLike.imageTintList = ColorStateList.valueOf(Color.WHITE)
                    } else {
                        icLike.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_favourite_like, null))
                        icLike.imageTintList = ColorStateList.valueOf(Color.WHITE)
                    }

                    icLike.setOnClickListener {
                        if (SharePreference.getBooleanPref(this@CategoryByFoodActivity, SharePreference.isLogin)) {
                            if (foodList!![position].isFavorite.equals("0")) {
                                val map = HashMap<String, String>()
                                map["item_id"] = foodList!![position].id!!.toString()
                                map["user_id"] = getStringPref(this@CategoryByFoodActivity, SharePreference.userId)!!
                                if (Common.isCheckNetwork(this@CategoryByFoodActivity)) {
                                    callApiFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                            this@CategoryByFoodActivity, resources.getString(R.string.no_internet))
                                }
                            }
                        } else {
                            openActivity(LoginActivity::class.java)
                            finish()
                        }

                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_foodsubcategory
                }

            }
            rvFoodData!!.adapter = foodAdapter
            rvFoodData!!.itemAnimator = DefaultItemAnimator()
            rvFoodData!!.isNestedScrollingEnabled = true
        } else {
            foodAdapter!!.notifyDataSetChanged()
        }
    }

    private fun callApiFavourite(hasmap: HashMap<String, String>, pos: Int) {
        Common.showLoadingProgress(this@CategoryByFoodActivity)
        val call = ApiClient.getClient.setAddFavorite(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        Common.dismissLoadingProgress()
                        foodList!!.removeAt(pos)
                        foodAdapter!!.notifyItemChanged(pos)
                        if (foodList!!.size == 0) {
                            rvFoodData!!.visibility = View.GONE
                            tvNoDataFound!!.visibility = View.VISIBLE
                        } else {
                            rvFoodData!!.visibility = View.VISIBLE
                            tvNoDataFound!!.visibility = View.GONE
                        }
                    } else if (restResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                                this@CategoryByFoodActivity,
                                restResponse.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        this@CategoryByFoodActivity,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }
}