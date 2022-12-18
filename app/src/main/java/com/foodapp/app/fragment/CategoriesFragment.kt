package com.foodapp.app.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foodapp.app.R
import com.foodapp.app.activity.CategoryByFoodActivity
import com.foodapp.app.activity.DashboardActivity
import com.foodapp.app.api.ApiClient
import com.foodapp.app.api.ListResponse
import com.foodapp.app.base.BaseAdaptor
import com.foodapp.app.base.BaseFragmnet
import com.foodapp.app.model.FoodCategoryModel
import com.foodapp.app.utils.Common
import kotlinx.android.synthetic.main.fragment_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesFragment : BaseFragmnet() {
    override fun setView(): Int = R.layout.fragment_category
    override fun Init(view: View) {
        ivMenu.setOnClickListener {
            (activity as DashboardActivity?)!!.onDrawerToggle()
        }
        if (Common.isCheckNetwork(activity!!)) {
            callApiCategoryFood()
        } else {
            Common.alertErrorOrValidationDialog(activity!!, resources.getString(R.string.no_internet))
        }
    }


    private fun callApiCategoryFood() {
        Common.showLoadingProgress(activity!!)
        val call = ApiClient.getClient.getFoodCategory()
        call.enqueue(object : Callback<ListResponse<FoodCategoryModel>> {
            override fun onResponse(
                    call: Call<ListResponse<FoodCategoryModel>>,
                    response: Response<ListResponse<FoodCategoryModel>>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponce: ListResponse<FoodCategoryModel> = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data!!.size > 0) {
                            if (isAdded) {
                                tvNoDataFound.visibility = View.GONE
                                rvFoodCategory.visibility = View.VISIBLE
                            }
                            setFoodCategoryAdaptor(restResponce.data!!)
                        } else {
                            if (isAdded) {
                                tvNoDataFound.visibility = View.VISIBLE
                                rvFoodCategory.visibility = View.GONE
                            }
                        }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(activity!!, restResponce.message)
                        if (isAdded) {
                            tvNoDataFound.visibility = View.VISIBLE
                            rvFoodCategory.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ListResponse<FoodCategoryModel>>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                        activity!!,
                        resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun setFoodCategoryAdaptor(foodCategoryList: ArrayList<FoodCategoryModel>) {
        val foodCategoryAdapter = object : BaseAdaptor<FoodCategoryModel>(activity!!, foodCategoryList) {
            @SuppressLint("ResourceType")
            override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FoodCategoryModel,
                    position: Int
            ) {
                val tvFoodCategoryName: TextView = holder!!.itemView.findViewById(R.id.tvFoodCategoryName)
                val ivFoodCategory: ImageView = holder.itemView.findViewById(R.id.ivFoodCategory)

                tvFoodCategoryName.text = foodCategoryList[position].getCategory_name()
                Glide.with(activity!!).load(foodCategoryList[position].getImage()).placeholder(ResourcesCompat.getDrawable(resources, R.drawable.ic_placeholder, null)).into(ivFoodCategory)
                holder.itemView.setOnClickListener {
                    startActivity(Intent(activity!!, CategoryByFoodActivity::class.java).putExtra("CategoryName", foodCategoryList[position].getCategory_name()).putExtra("CategoryId", foodCategoryList[position].getId()))
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_foodoutcategory
            }

        }
        if (isAdded) {
            rvFoodCategory.apply {
                adapter = foodCategoryAdapter
                layoutManager = GridLayoutManager(activity!!, 3, GridLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
                isNestedScrollingEnabled = true
            }
        }
    }
}