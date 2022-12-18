package com.foodapp.app.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.res.ResourcesCompat
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
import com.foodapp.app.model.FoodItemModel
import com.foodapp.app.model.SearchItemModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.SharePreference
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.row_search_item_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SearchActivity : BaseActivity() {
    var strSearch: String = ""
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var foodAdapter: BaseAdaptor<FoodItemModel>? = null
    var searchAdapter: BaseAdaptor<SearchItemModel>? = null
    var foodList: ArrayList<FoodItemModel>? = null
    var searchList = ArrayList<SearchItemModel>()
    var newSearchData = ArrayList<SearchItemModel>()
    var manager1: LinearLayoutManager? = null
    override fun setLayout(): Int {
        return R.layout.activity_search
    }

    override fun InitView() {
        Common.getCurrentLanguage(this@SearchActivity, false)
        foodList = ArrayList()
        manager1 = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

//        manager1 = GridLayoutManager(this@SearchActivity, 2, GridLayoutManager.VERTICAL, false)
//        rvSearchOrder.layoutManager = manager1
        ivBack.setOnClickListener {
            val intent =
                Intent(this@SearchActivity, DashboardActivity::class.java).putExtra("pos", "1")
            startActivity(intent)
            finish()
            finishAffinity()
        }
        setupAdapter(newSearchData)

        callApiSearchFood()

     /*   edSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })*/
        if (SharePreference.getStringPref(this@SearchActivity, SharePreference.SELECTED_LANGUAGE)
                .equals(resources.getString(R.string.language_hindi))
        ) {
            ivBack.rotation = 180F
        } else {
            ivBack.rotation = 0F
        }

        var visibleItemCount: Int = 0
        var totalItemCount: Int = 0
        var pastVisiblesItems: Int = 0

        rvSearchOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager1!!.childCount
                    totalItemCount = manager1!!.itemCount
                    pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                    if (CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            if (Common.isCheckNetwork(this@SearchActivity)) {
                                callApiSearchFood()
                            } else {
                                Common.alertErrorOrValidationDialog(
                                    this@SearchActivity,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })



        edSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                val text: String = edSearch.text.toString().toLowerCase(Locale.getDefault())
                filter(text)
                if(edSearch.text.isEmpty())
                {
                    newSearchData.clear()
                    searchAdapter?.notifyDataSetChanged()
                    if(newSearchData.isEmpty())
                    {
                        tvNoDataFound.visibility=View.VISIBLE
                        rvSearchOrder.visibility=View.GONE
                    }else
                    {
                        tvNoDataFound.visibility=View.GONE
                        rvSearchOrder.visibility=View.VISIBLE
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                arg0: CharSequence, arg1: Int, arg2: Int,
                arg3: Int
            ) {

            }
        })
    }


    fun filter(text: String) {
        Log.d("tag ::", "mainitemList 02" + searchList.size)

        newSearchData.clear()
        for (d in searchList) {
            if (d.itemName?.toLowerCase(Locale.ROOT)?.contains(text.toLowerCase(Locale.ROOT))!!) {
                newSearchData.add(d)
                            Log.d("tag ::", "itemList 02" + newSearchData.size)

            }
//            Log.d("tag ::", "itemList 02" + itemList.size())
        }
        searchAdapter?.notifyDataSetChanged()

        if(newSearchData.isEmpty())
        {
            tvNoDataFound.visibility=View.VISIBLE
            rvSearchOrder.visibility=View.GONE
        }else
        {
            tvNoDataFound.visibility=View.GONE
            rvSearchOrder.visibility=View.VISIBLE
        }
    }
    private fun performSearch(strSearch: String) {
        this.strSearch = strSearch
        if (Common.isCheckNetwork(this@SearchActivity)) {
            callApiSearchFood()
        } else {
            Common.alertErrorOrValidationDialog(
                this@SearchActivity,
                resources.getString(R.string.no_internet)
            )
        }
    }

    private fun callApiSearchFood() {

        Common.showLoadingProgress(this@SearchActivity)
        val map = HashMap<String, String>()
//        map.put("keyword", strSearch!!)
        map.put(
            "user_id",
            SharePreference.getStringPref(this@SearchActivity, SharePreference.userId)!!
        )
        val call = ApiClient.getClient.setSearch()
        call.enqueue(object : Callback<ListResponse<SearchItemModel>> {
            override fun onResponse(
                call: Call<ListResponse<SearchItemModel>>,
                response: Response<ListResponse<SearchItemModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: ListResponse<SearchItemModel> = response.body()!!
                    if (restResponce.status==1) {
                        tvNoDataFound.visibility = View.VISIBLE
                        rvSearchOrder.visibility = View.GONE
                        Common.dismissLoadingProgress()
                        restResponce.data?.let { searchList.addAll(it) }
                        Log.e("searchList", searchList.size.toString())
                        /*CURRENT_PAGES = foodItemResponseModel.currentPage!!.toInt()
                        TOTAL_PAGES = foodItemResponseModel.lastPage!!.toInt()*/
                        /*   if (foodItemResponseModel.data!!.size > 0) {
                               for (i in 0 until foodItemResponseModel.data.size) {
                                   val foodItemModel = FoodItemModel()
                                   foodItemModel.id= foodItemResponseModel.data.get(i).id
                                   foodItemModel.itemName= foodItemResponseModel.data[i].itemName

                                   foodItemModel.variation= foodItemResponseModel.data[i].variation

                                   foodItemModel.itemimage= foodItemResponseModel.data[i].itemimage

                                   foodItemModel.isFavorite= foodItemResponseModel.data[i].isFavorite

                                   foodList!!.add(foodItemModel)
                               }*/
//                            setFoodAdaptor()
                    } else {
                        tvNoDataFound.visibility = View.VISIBLE
                        rvSearchOrder.visibility = View.GONE
                        Common.dismissLoadingProgress()

                    }
                } else {
                    Common.dismissLoadingProgress()
                }
            }


            override fun onFailure(call: Call<ListResponse<SearchItemModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@SearchActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this@SearchActivity, DashboardActivity::class.java).putExtra("pos", "1")
        startActivity(intent)
        finish()
        finishAffinity()
    }

/*
    fun setFoodAdaptor() {
        foodAdapter = object : BaseAdaptor<FoodItemModel>(this@SearchActivity, foodList!!) {
            @SuppressLint("NewApi")
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
                tvFoodName.text = foodList!!.get(position).itemName
                tvFoodPriceGrid.text = Common.getCurrancy(this@SearchActivity)
                    .plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            foodList!!.get(position).variation?.get(0)?.productPrice!!.toDouble()
                        )
                    )
                Glide.with(this@SearchActivity)
                    .load(foodList!!.get(position).itemimage!!.image)
                    .placeholder(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_placeholder,
                            null
                        )
                    ).into(ivFood)
                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(
                            this@SearchActivity,
                            FoodDetailActivity::class.java
                        ).putExtra("foodItemId", foodList!!.get(position).id)
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
                            this@SearchActivity,
                            SharePreference.isLogin
                        )
                    ) {
                        if (foodList!!.get(position).isFavorite.equals("0")) {
                            val map = HashMap<String, String>()
                            map["item_id"] = foodList!!.get(position).id!!
                            if (SharePreference.getBooleanPref(
                                    this@SearchActivity,
                                    SharePreference.isLogin
                                )
                            ) {
                                map["user_id"] = SharePreference.getStringPref(
                                    this@SearchActivity,
                                    SharePreference.userId
                                )!!
                            } else {
                                map["user_id"] = ""
                            }
                            if (Common.isCheckNetwork(this@SearchActivity)) {
                                callApiFavourite(map, position)
                            } else {
                                Common.alertErrorOrValidationDialog(
                                    this@SearchActivity,
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
                return R.layout.row_foodsubcategory
            }

        }
        rvSearchOrder.adapter = foodAdapter
        rvSearchOrder.itemAnimator = DefaultItemAnimator()
        rvSearchOrder.isNestedScrollingEnabled = true

    }
*/

    private fun callApiFavourite(hasmap: HashMap<String, String>, pos: Int) {
        Common.showLoadingProgress(this@SearchActivity)
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
                        foodList!!.get(pos).isFavorite = "1"
                        foodAdapter!!.notifyDataSetChanged()

                    } else if (restResponse.getStatus().equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@SearchActivity,
                            restResponse.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@SearchActivity,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    private fun setupAdapter(searchList: ArrayList<SearchItemModel>) {
        searchAdapter = object : BaseAdaptor<SearchItemModel>(this@SearchActivity, searchList) {
            @SuppressLint("NewApi")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: SearchItemModel,
                position: Int
            ) {

                val tvItemName: TextView = holder!!.itemView.findViewById(R.id.tvItemName)
                tvItemName.text = searchList[position].itemName
                holder.itemView.setOnClickListener {
                    startActivity(
                        Intent(this@SearchActivity, FoodDetailActivity::class.java)
                            .putExtra("foodItemId", searchList[position].id.toString())
                    )


                }

            }

            override fun setItemLayout(): Int {
                return R.layout.row_search_item_list
            }

        }
        rvSearchOrder.adapter = searchAdapter
        rvSearchOrder.layoutManager=LinearLayoutManager(this@SearchActivity,RecyclerView.VERTICAL,false)
        rvSearchOrder.itemAnimator = DefaultItemAnimator()
        rvSearchOrder.isNestedScrollingEnabled = true
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@SearchActivity, false)
    }

}