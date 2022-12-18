package com.foodapp.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.*
import com.foodapp.app.R
import com.foodapp.app.activity.*
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.ListResponse
import com.foodapp.app.api.RestResponse
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.base.BaseFragmnet
import com.foodapp.app.model.*
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.callApiLocation
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.utils.SharePreference
import com.foodapp.app.utils.SharePreference.Companion.isCurrancy
import com.foodapp.app.utils.SharePreference.Companion.isLinearLayoutManager
import com.foodapp.app.utils.SharePreference.Companion.setStringPref
import com.bumptech.glide.Glide
import com.foodapp.app.utils.SharePreference.Companion.isMaximum
import com.foodapp.app.utils.SharePreference.Companion.isMiniMum
import com.foodapp.app.utils.SharePreference.Companion.isMiniMumQty
import com.foodapp.app.utils.SharePreference.Companion.mapKey
import com.foodapp.app.utils.SharePreference.Companion.userRefralAmount
import kotlinx.android.synthetic.main.activity_foodorderdetail.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.ivCart
import kotlinx.android.synthetic.main.fragment_home.ivMenu
import kotlinx.android.synthetic.main.fragment_home.swiperefresh
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : BaseFragmnet() {
    var timer: Timer? = null
    var foodCategoryAdapter: BaseAdaptor<FoodCategoryModel>? = null
    private var foodCategoryList: ArrayList<FoodCategoryModel>? = null
    var bannerList: ArrayList<BannerModel>? = null
    var bannerAdapter: BaseAdaptor<BannerModel>? = null
    var foodAdapter: BaseAdaptor<FoodItemModel>? = null
    private var foodList: ArrayList<FoodItemModel>? = null
    var foodCategoryId = "";
    var manager1: GridLayoutManager? = null
    var CurrentPageNo: Int = 1
    var TOTAL_PAGES: Int = 0
    var isLoding: Boolean = true
    var scrollView: NestedScrollView? = null
    var rlCount: RelativeLayout? = null
    var pos = 0
    var tvCount: TextView? = null
    var bannerDatalist=ArrayList<BannerModel>()
//    var timer: Timer? = null
    override fun setView(): Int {
        return R.layout.fragment_home
    }

    override fun Init(view: View) {
        Common.getCurrentLanguage(requireActivity(), false)
        foodList = ArrayList()
        scrollView = view.findViewById(R.id.scrollView)
        rlCount = view.findViewById(R.id.rlCount)
        tvCount = view.findViewById(R.id.tvCount)

        if (SharePreference.getBooleanPref(requireActivity(), isLinearLayoutManager)) {
            manager1 = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
            ic_grid?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_listitem,
                    null
                )
            )
        } else {
            manager1 = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
            ic_grid?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_grid,
                    null
                )
            )
        }

        rvFoodSubcategory?.layoutManager = manager1

        if (isCheckNetwork(requireActivity())) {
            callApiBanner()
        } else {
            alertErrorOrValidationDialog(
                requireActivity(),
                resources.getString(R.string.no_internet)
            )
        }

        ivMenu?.setOnClickListener {
            (activity as DashboardActivity?)?.onDrawerToggle()
        }
        ivCart.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                openActivity(CartActivity::class.java)
            } else {
                openActivity(LoginActivity::class.java)
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }
        ivSearch.setOnClickListener {
            openActivity(SearchActivity::class.java)
            requireActivity().finish()
        }

        ic_grid.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), isLinearLayoutManager)) {
                manager1 = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                SharePreference.setBooleanPref(activity!!, isLinearLayoutManager, false)
                ic_grid.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_grid,
                        null
                    )
                )
            } else {
                manager1 = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
                SharePreference.setBooleanPref(activity!!, isLinearLayoutManager, true)
                ic_grid.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_listitem,
                        null
                    )
                )
            }
            rvFoodSubcategory.layoutManager = manager1
        }

        if (isAdded) {
            scrollView!!.viewTreeObserver.addOnScrollChangedListener {
                val view1 = scrollView!!.getChildAt(scrollView!!.childCount - 1) as View
                val diff: Int =
                    view1.bottom - (scrollView!!.height + scrollView!!.scrollY)
                if (diff == 0 && CurrentPageNo < TOTAL_PAGES) {
                    isLoding = false
                    CurrentPageNo += 1
                    if (isCheckNetwork(activity!!)) {
                        callApiFood(
                            foodCategoryId,
                            isFirstTime = false,
                            isSelect = false,
                            isFristTimeSelect = false
                        )
                    } else {
                        alertErrorOrValidationDialog(
                            activity!!,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
            }
        }

        ivLocation.setOnClickListener {
            if (isCheckNetwork(activity!!)) {
                callApiLocation(activity!!)
            } else {
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.no_internet)
                )
            }
        }

        swiperefresh.setOnRefreshListener { // Your code to refresh the list here.
            if (isCheckNetwork(activity!!)) {
                swiperefresh.isRefreshing = false
                foodList!!.clear()
                isLoding = true
                CurrentPageNo = 1
                pos=0
                timer?.cancel()
                TOTAL_PAGES = 0

                if (isCheckNetwork(activity!!)) {
                bannerList?.clear()
                    callApiBanner()
                } else {
                    alertErrorOrValidationDialog(
                        activity!!,
                        resources.getString(R.string.no_internet)
                    )
                }
            } else {
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.no_internet)
                )
            }
        }




    }


    private fun callApiBanner() {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.getBanner()
        call.enqueue(object : Callback<ListResponse<BannerModel>> {
            override fun onResponse(
                call: Call<ListResponse<BannerModel>>,
                response: Response<ListResponse<BannerModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<BannerModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data!!.size > 0) {
                            bannerList = restResponce.data
                            bannerDatalist.addAll(restResponce.data!!)
                            callApiCategoryFood()
                        } else {
                            callApiCategoryFood()
                        }
                    } else if (restResponce.status == 0) {
                        callApiCategoryFood()
                    }
                } else {
                    callApiCategoryFood()
                }
            }

            override fun onFailure(call: Call<ListResponse<BannerModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    private fun callApiCategoryFood() {
        val call = ApiClient.getClient.getFoodCategory()
        call.enqueue(object : Callback<ListResponse<FoodCategoryModel>> {
            override fun onResponse(
                call: Call<ListResponse<FoodCategoryModel>>,
                response: Response<ListResponse<FoodCategoryModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<FoodCategoryModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data!!.size > 0) {
                            foodCategoryList = restResponce.data!!
                            foodCategoryId = foodCategoryList!![0].getId()!!
                            foodCategoryList!![0].setSelect(true)
                            callApiFood(
                                foodCategoryId,
                                isFirstTime = true,
                                isSelect = false,
                                isFristTimeSelect = true
                            )
                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<FoodCategoryModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiFood(
        id: String?,
        isFirstTime: Boolean,
        isSelect: Boolean,
        isFristTimeSelect: Boolean
    ) {
        if (!isFirstTime) {
            showLoadingProgress(requireActivity())
        }
        if (isSelect) {
            showLoadingProgress(requireActivity())
            foodList!!.clear()
        }
        val map = HashMap<String, String>()
        map["cat_id"] = id!!
        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            map["user_id"] =
                SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!
        }
        val call = ApiClient.getClient.getFoodItem(map, CurrentPageNo.toString())
        call.enqueue(object : Callback<RestResponse<FoodItemResponseModel>> {
            override fun onResponse(
                call: Call<RestResponse<FoodItemResponseModel>>,
                response: Response<RestResponse<FoodItemResponseModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<FoodItemResponseModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        if (!isFristTimeSelect) {
                            dismissLoadingProgress()
                        }
                        val foodItemResponseModel: FoodItemResponseModel = restResponce.getData()!!
                        CurrentPageNo = foodItemResponseModel.currentPage!!.toInt()
                        TOTAL_PAGES = foodItemResponseModel.lastPage!!.toInt()
                        for (i in 0 until foodItemResponseModel.data!!.size) {
                            val foodItemModel = FoodItemModel()
                            foodItemModel.id = foodItemResponseModel.data!!.get(i).id
                            foodItemModel.itemName = foodItemResponseModel.data!![i].itemName

                            foodItemModel.variation = foodItemResponseModel.data!![i].variation

                            foodItemModel.itemimage = foodItemResponseModel.data!![i].itemimage

                            foodItemModel.isFavorite = foodItemResponseModel.data!![i].isFavorite

                            foodList!!.add(foodItemModel)
                        }

                        setStringPref(
                            activity!!,
                            userRefralAmount,
                            restResponce.getReferral_amount()!!
                        )
                        setStringPref(
                            activity!!,
                            isCurrancy,
                            restResponce.getCurrency()!!
                        )
                        setStringPref(
                            activity!!,
                            isMiniMum,
                            restResponce.getMin_order_amount()!!
                        )
                        setStringPref(
                            activity!!,
                            isMaximum,
                            restResponce.getMax_order_amount()!!
                        )
                        setStringPref(
                            activity!!,
                            isMiniMumQty,
                            restResponce.getMax_order_qty()!!
                        )
                        setStringPref(
                            activity!!,
                            mapKey,
                            restResponce.getMap().toString()
                        )

                        setFoodAdaptor(isFirstTime, isFristTimeSelect)
                        if (isFristTimeSelect) {
                            if (SharePreference.getBooleanPref(
                                    activity!!,
                                    SharePreference.isLogin
                                )
                            ) {
                                if (isCheckNetwork(activity!!)) {
                                    callApiCartCount(isFristTime = true, isOnResume = false)
                                } else {
                                    alertErrorOrValidationDialog(
                                        activity!!,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            } else {
                                dismissLoadingProgress()
                                rlCount!!?.visibility = View.GONE
                            }
                        }
                    } else if (restResponce.getMessage().equals("0")) {
                        if (!isFirstTime) {
                            dismissLoadingProgress()
                            alertErrorOrValidationDialog(
                                activity!!,
                                restResponce.getMessage()
                            )
                        }

                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        dismissLoadingProgress()
                        Common.setLogout(activity!!)
                    } else {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            error.getString("message")
                        )
                    }
                }

            }

            override fun onFailure(call: Call<RestResponse<FoodItemResponseModel>>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun setFoodCategoryAdaptor() {
        foodCategoryAdapter =
            object : BaseAdaptor<FoodCategoryModel>(activity!!, foodCategoryList!!) {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                @SuppressLint("ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FoodCategoryModel,
                    position: Int
                ) {
                    val tvFoodCategoryName: TextView =
                        holder!!.itemView.findViewById(R.id.tvFoodCategoryName)
                    val ivFoodCategory: ImageView =
                        holder.itemView.findViewById(R.id.ivFoodCategory)
                    val llBarger: LinearLayout = holder.itemView.findViewById(R.id.llBarger)
                    val ViewFrist: View = holder.itemView.findViewById(R.id.ViewFrist)
                    val ViewLast: View = holder.itemView.findViewById(R.id.ViewLast)

                    when (position) {
                        0 -> {
                            ViewFrist.visibility = View.VISIBLE
                            ViewLast.visibility = View.GONE
                        }
                        (foodCategoryList!!.size - 1) -> {
                            ViewFrist.visibility = View.GONE
                            ViewLast.visibility = View.VISIBLE
                        }
                        else -> {
                            ViewFrist.visibility = View.GONE
                            ViewLast.visibility = View.GONE
                        }
                    }

                    if (foodCategoryList!!.get(position).isSelect()!!) {
                        llBarger.background = ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.bg_strock_orange5,
                            null
                        )
                    } else {
                        llBarger.background = null
                    }
                    tvFoodCategoryName.text = foodCategoryList!!.get(position).getCategory_name()
                    Glide.with(activity!!).load(foodCategoryList!!.get(position).getImage())
                        .placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_placeholder,
                                null
                            )
                        )
                        .into(ivFoodCategory)
                    holder.itemView.setOnClickListener {
                        for (i in 0 until foodCategoryList!!.size) {
                            foodCategoryList!!.get(i).setSelect(false)
                        }
                        foodCategoryList!!.get(position).setSelect(true)
                        foodCategoryId = foodCategoryList!!.get(position).getId()!!
                        foodList!!.clear()
                        notifyDataSetChanged()
                        isLoding = true
                        CurrentPageNo = 1
                        TOTAL_PAGES = 0
                        if (SharePreference.getBooleanPref(activity!!, isLinearLayoutManager)) {
                            manager1 =
                                GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
                            ic_grid.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_listitem,
                                    null
                                )
                            )
                        } else {
                            manager1 =
                                GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
                            ic_grid.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_grid,
                                    null
                                )
                            )
                        }
                        rvFoodSubcategory.layoutManager = manager1
                        if (isCheckNetwork(activity!!)) {
                            callApiFood(foodCategoryId, true, true, false)
                        } else {
                            alertErrorOrValidationDialog(
                                activity!!,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_foodcategory
                }

            }
        if (isAdded) {
            rvFoodCategory?.adapter = foodCategoryAdapter
            rvFoodCategory?.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            rvFoodCategory?.itemAnimator = DefaultItemAnimator()
            rvFoodCategory?.isNestedScrollingEnabled = true
        }
    }

    fun setFoodAdaptor(isFirstTime: Boolean, fristTimeSelect: Boolean) {
        if (isFirstTime) {
            if (fristTimeSelect) {
                setFoodCategoryAdaptor()
                if (bannerList != null) {
                    rlBenner?.visibility = View.VISIBLE
                    loadPagerImages(bannerList!!)
                } else {
                    rlBenner?.visibility = View.GONE
                }
            }
            foodAdapter = object : BaseAdaptor<FoodItemModel>(activity!!, foodList!!) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FoodItemModel,
                    position: Int
                ) {
                    val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                    val tvFoodPriceGrid: TextView =
                        holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                    val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                    val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                    val tvFoodOnSale: TextView = holder.itemView.findViewById(R.id.tvFoodOnSale)

                    tvFoodName.text = foodList!![position].itemName
                    Common.getPrice(
                        foodList!![position].variation?.get(0)?.productPrice!!.toDouble(),
                        tvFoodPriceGrid,
                        activity!!
                    )
                    if (SharePreference.getBooleanPref(activity!!, isLinearLayoutManager)) {
                        Glide.with(activity!!)
                            .load(foodList!![position].itemimage?.image).centerCrop()
                            .placeholder(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_placeholder,
                                    null
                                )
                            )
                            .into(ivFood)
                    } else {
                        Glide.with(activity!!)
                            .load(foodList!![position].itemimage!!.image).centerCrop()
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
                                activity!!,
                                FoodDetailActivity::class.java
                            ).putExtra("foodItemId", foodList!![position].id)
                        )
                    }


                    if (foodList!![position].variation?.get(0)?.salePrice != 0) {
                        tvFoodOnSale.visibility = View.VISIBLE
                    } else {
                        tvFoodOnSale.visibility = View.GONE

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
                        if (SharePreference.getBooleanPref(activity!!, SharePreference.isLogin)) {
                            if (foodList!!.get(position).isFavorite.equals("0")) {
                                val map = HashMap<String, String>()
                                map["item_id"] = foodList!!.get(position).id!!
                                map["user_id"] = SharePreference.getStringPref(
                                    activity!!,
                                    SharePreference.userId
                                )!!
                                if (isCheckNetwork(activity!!)) {
                                    callApiFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        activity!!,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(LoginActivity::class.java)
                            activity!!.finish()
                        }

                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_foodsubcategory
                }

            }
            if (isAdded) {
                rvFoodSubcategory?.adapter = foodAdapter
                rvFoodSubcategory?.itemAnimator = DefaultItemAnimator()
            }
        } else {
            foodAdapter!!.notifyDataSetChanged()
        }
    }

    private fun callApiFavourite(hasmap: HashMap<String, String>, pos: Int) {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.setAddFavorite(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        foodList!!.get(pos).isFavorite = "1"
                        foodAdapter!!.notifyDataSetChanged()
                    } else if (restResponse.getStatus().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponse.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiCartCount(isFristTime: Boolean, isOnResume: Boolean) {
        if (!isFristTime) {
            showLoadingProgress(activity!!)
        }
        val map = HashMap<String, String>()
        map.put("user_id", SharePreference.getStringPref(activity!!, SharePreference.userId)!!)
        val call = ApiClient.getClient.getCartCount(map)
        call.enqueue(object : Callback<CartCountModel> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<CartCountModel>,
                response: Response<CartCountModel>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce: CartCountModel = response.body()!!
                    if (restResponce.getStatus().equals("0")) {
                        tvCount!!.text = "0"
                    } else {
                        tvCount!!.text = restResponce.getCart()
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        activity!!,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<CartCountModel>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun loadPagerImages(imageHase: ArrayList<BannerModel>) {
        bannerAdapter = object : BaseAdaptor<BannerModel>(activity!!, imageHase) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: BannerModel,
                position: Int
            ) {

                val ivFood: ImageView = holder!!.itemView.findViewById(R.id.ivBannereSlider)
                Glide.with(activity!!).load(imageHase.get(position).getImage()).placeholder(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_placeholder,
                        null
                    )
                ).into(ivFood)

                /*   val view1: View = holder.itemView.findViewById(R.id.view1)
                   if (position == 0) {
                       view1.visibility = View.VISIBLE
                   } else {
                       view1.visibility = View.GONE
                   }
   */
                holder.itemView.setOnClickListener {
                    if (`val`.getType().toString() == "item") {
                        startActivity(
                            Intent(
                                activity!!,
                                FoodDetailActivity::class.java
                            ).putExtra("foodItemId", `val`.getItemId().toString())
                                .putExtra("isItemActivity", resources.getString(R.string.home))
                        )
                    } else if (`val`.getType().toString() == "category") {
                        startActivity(
                            Intent(activity!!, CategoryByFoodActivity::class.java).putExtra(
                                "CategoryId",
                                `val`.getCatId().toString()
                            ).putExtra("CategoryName", `val`.getCategoryName().toString())
                        )
                    }
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_bannerslider
            }

        }
        rvBanner?.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        rvBanner?.adapter = bannerAdapter
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(null)
        rvBanner?.onFlingListener = null
        rvBanner?.let { snapHelper.attachToRecyclerView(it) }
        timer = Timer()
        rvBanner?.let { timer?.schedule(AutoScrollTask(pos, it, imageHase), 0, 5000L) }
    }




    private class AutoScrollTask(
        private var position: Int,
        private var rvBanner: RecyclerView,
        private var arrayList: ArrayList<BannerModel>
    ) : TimerTask() {
        override fun run() {
            if (arrayList.size > position) {

                if (position == arrayList.size - 1) {
                    position = 0
                } else {
                    position++
                }

            }
            rvBanner.smoothScrollToPosition(position)
        }
    }

    override fun onPause() {
        super.onPause()
        if(timer!=null)
        {
            timer!!.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        if(timer!=null)
        {
            if(bannerDatalist!=null)
            {
                timer=Timer()
                rvBanner?.let { timer?.schedule(AutoScrollTask(pos, it, bannerDatalist), 0, 5000L) }
            }


        }
        Common.getCurrentLanguage(activity!!, false)
        if (SharePreference.getBooleanPref(activity!!, SharePreference.isLogin)) {
            if (Common.isCartTrueOut) {
                if (isCheckNetwork(activity!!)) {
                    Common.isCartTrueOut = false
                    callApiCartCount(false, true)
                } else {
                    alertErrorOrValidationDialog(
                        activity!!,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        } else {
            rlCount!!.visibility = View.GONE
        }

    }
}