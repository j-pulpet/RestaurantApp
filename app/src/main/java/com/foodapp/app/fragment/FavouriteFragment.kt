package com.foodapp.app.fragment

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
import com.foodapp.app.R
import com.foodapp.app.activity.DashboardActivity
import com.foodapp.app.activity.FoodDetailActivity
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.RestResponse
import com.foodapp.app.api.SingleResponse
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.base.BaseFragmnet
import com.foodapp.app.model.FavouriteFoodModel
import com.foodapp.app.model.FoodFavouriteResponseModel
import com.foodapp.app.utils.Common
import com.foodapp.app.utils.Common.alertErrorOrValidationDialog
import com.foodapp.app.utils.Common.dismissLoadingProgress
import com.foodapp.app.utils.Common.isCheckNetwork
import com.foodapp.app.utils.Common.showLoadingProgress
import com.foodapp.app.utils.SharePreference
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_favourite.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FavouriteFragment:BaseFragmnet(){
    var CURRENT_PAGES: Int = 1
    var TOTAL_PAGES: Int = 0
    var foodAdapter: BaseAdaptor<FavouriteFoodModel>? = null
    var foodList: ArrayList<FavouriteFoodModel>? = null
    var manager1: GridLayoutManager? = null
    var rvFavourite: RecyclerView? = null
    var tvNoDataFound: TextView? = null
    override fun setView(): Int {
       return R.layout.fragment_favourite
    }
    override fun Init(view: View) {
        Common.getCurrentLanguage(activity!!, false)
        foodList=ArrayList()
        rvFavourite=view.findViewById(R.id.rvFavourite)
        tvNoDataFound=view.findViewById(R.id.tvNoDataFound)
        manager1= GridLayoutManager(activity,2, GridLayoutManager.VERTICAL,false)
        rvFavourite!!.layoutManager=manager1
        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }
        if (isCheckNetwork(activity!!)) {
            callApiFavouriteFood(true)
        } else {
            alertErrorOrValidationDialog(
                activity!!,
                resources.getString(R.string.no_internet)
            )
        }

        var visibleItemCount: Int = 0
        var totalItemCount: Int = 0
        var pastVisiblesItems: Int = 0

        rvFavourite!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = manager1!!.getChildCount()
                    totalItemCount = manager1!!.getItemCount()
                    pastVisiblesItems = manager1!!.findFirstVisibleItemPosition()
                    if (CURRENT_PAGES < TOTAL_PAGES) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            CURRENT_PAGES += 1
                            if (Common.isCheckNetwork(activity!!)) {
                                callApiFavouriteFood(false)
                            } else {
                                Common.alertErrorOrValidationDialog(
                                    activity!!,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    private fun callApiFavouriteFood(isFirstTime: Boolean?) {
        showLoadingProgress(activity!!)
        val map = HashMap<String, String>()
        map.put("user_id", SharePreference.getStringPref(activity!!, SharePreference.userId)!!)
        val call = ApiClient.getClient.getFavouriteList(map, CURRENT_PAGES.toString())
        call.enqueue(object : Callback<RestResponse<FoodFavouriteResponseModel>> {
            override fun onResponse(
                call: Call<RestResponse<FoodFavouriteResponseModel>>,
                response: Response<RestResponse<FoodFavouriteResponseModel>>
            ) {
                if (response.code() == 200) {
                    val restResponce: RestResponse<FoodFavouriteResponseModel> = response.body()!!
                    if (restResponce.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        val foodItemResponseModel: FoodFavouriteResponseModel = restResponce.getData()!!
                        CURRENT_PAGES = foodItemResponseModel.getCurrent_page()!!.toInt()
                        TOTAL_PAGES = foodItemResponseModel.getLast_page()!!.toInt()
                        if(foodItemResponseModel.getData()!!.size>0){
                            rvFavourite!!.visibility=View.VISIBLE
                            tvNoDataFound!!.visibility=View.GONE
                            for (i in 0 until foodItemResponseModel.getData()!!.size) {
                                val foodItemModel = FavouriteFoodModel()
                                foodItemModel.setId(foodItemResponseModel.getData()!!.get(i).getId())
                                foodItemModel.setItem_name(
                                    foodItemResponseModel.getData()!!.get(i).getItem_name()
                                )
                                foodItemModel.setItem_price(
                                    foodItemResponseModel.getData()!!.get(i).getItem_price()
                                )
                                foodItemModel.setItemimage(
                                    foodItemResponseModel.getData()!!.get(i).getItemimage()
                                )
                                foodItemModel.setFavorite_id(
                                    foodItemResponseModel.getData()!!.get(i).getFavorite_id()
                                )
                                foodList!!.add(foodItemModel)
                            }
                            setFoodAdaptor(isFirstTime!!)
                        }else{
                            rvFavourite!!.visibility=View.GONE
                            tvNoDataFound!!.visibility=View.VISIBLE
                        }

                    } else if (restResponce.getMessage().equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            activity!!,
                            restResponce.getMessage()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<RestResponse<FoodFavouriteResponseModel>>, t: Throwable) {
               dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    activity!!,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
    fun setFoodAdaptor(isFirstTime:Boolean) {
        if(isFirstTime){
            foodAdapter = object : BaseAdaptor<FavouriteFoodModel>(activity!!,foodList!!) {
                @SuppressLint("NewApi")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FavouriteFoodModel,
                    position: Int
                ) {
                    val tvFoodName: TextView = holder!!.itemView.findViewById(R.id.tvFoodName)
                    val tvFoodPriceGrid: TextView = holder.itemView.findViewById(R.id.tvFoodPriceGrid)
                    val ivFood: ImageView = holder.itemView.findViewById(R.id.ivFood)
                    val icLike: ImageView = holder.itemView.findViewById(R.id.icLike)
                    tvFoodName.text = foodList!!.get(position).getItem_name()
                    tvFoodPriceGrid.text = Common.getCurrancy(activity!!).plus(String.format(Locale.US,"%,.2f",foodList!!.get(position).getItem_price()!!.toDouble()))
                    Glide.with(activity!!).load(foodList!!.get(position).getItemimage()!!.getImage()).placeholder(ResourcesCompat.getDrawable(resources,R.drawable.placeholder,null)).into(ivFood)
                    holder.itemView.setOnClickListener {
                        startActivity(Intent(activity!!, FoodDetailActivity::class.java).putExtra("foodItemId",foodList!!.get(position).getId()))
                    }
                    tvFoodPriceGrid.visibility= View.VISIBLE

                    icLike.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_favourite_like,null))
                    icLike.imageTintList= ColorStateList.valueOf(Color.WHITE)

                    icLike.setOnClickListener {
                        val map = HashMap<String, String>()
                        map.put("favorite_id",foodList!!.get(position).getFavorite_id()!!)
                        map.put("user_id",SharePreference.getStringPref(activity!!,SharePreference.userId)!!)
                        if(isCheckNetwork(activity!!)){
                            callApiFavourite(map,position)
                        }else{
                            alertErrorOrValidationDialog(
                                    activity!!,
                                    resources.getString(R.string.no_internet)
                            )
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_foodsubcategory
                }

            }
            if(isAdded){
                rvFavourite!!.adapter = foodAdapter
                rvFavourite!!.itemAnimator = DefaultItemAnimator()
                rvFavourite!!.isNestedScrollingEnabled = true
            }
        }else{
            foodAdapter!!.notifyDataSetChanged()
        }
    }

    private fun callApiFavourite(hasmap:HashMap<String, String>,pos:Int) {
        showLoadingProgress(activity!!)
        val call = ApiClient.getClient.setRemovefavorite(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.getStatus().equals("1")) {
                        dismissLoadingProgress()
                        foodList!!.removeAt(pos)
                        foodAdapter!!.notifyDataSetChanged()






                        if(foodList!!.size==0){
                            rvFavourite!!.visibility=View.GONE
                            tvNoDataFound!!.visibility=View.VISIBLE
                        }else{
                            rvFavourite!!.visibility=View.VISIBLE
                            tvNoDataFound!!.visibility=View.GONE
                        }
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

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(activity!!, false)
    }
}