package com.foodapp.app.api

import com.foodapp.app.model.*
import com.google.gson.JsonObject
import com.grocery.app.api.ListResopone
import com.grocery.app.model.PinCodeResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("login")
    fun getLogin(@Body map: HashMap<String, String>): Call<RestResponse<LoginModel>>

    @POST("register")
    fun setRegistration(@Body map: HashMap<String, String>): Call<RestResponse<RegistrationModel>>

    @POST("getprofile")
    fun getProfile(@Body map: HashMap<String, String>): Call<GetProfileResponse>

    @Multipart
    @POST("editprofile")
    fun setProfile(@Part("user_id") userId: RequestBody, @Part("name") name: RequestBody, @Part profileimage: MultipartBody.Part?): Call<SingleResponse>

    @POST("changepassword")
    fun setChangePassword(@Body map: HashMap<String, String>): Call<SingleResponse>

    @GET("category")
    fun getFoodCategory(): Call<ListResponse<FoodCategoryModel>>

    @POST("item")
    fun getFoodItem(@Body map: HashMap<String, String>, @Query("page") strPageNo: String): Call<RestResponse<FoodItemResponseModel>>

    @POST("orderhistory")
    fun getOrderHistory(@Body map: HashMap<String, String>): Call<ListResponse<OrderHistoryModel>>

    @GET("rattinglist")
    fun getRatting(): Call<ListResponse<RattingModel>>

    @POST("getcart")
    fun getCartItem(@Body map: HashMap<String, String>): Call<ListResponse<CartItemModel>>

    @POST("ratting")
    fun setRatting(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("itemdetails")
    fun setItemDetail(@Body map: HashMap<String, String>): Call<RestResponse<ItemDetailModel>>

    @POST("cart")
    fun setAddToCart(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("qtyupdate")
    fun setQtyUpdate(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("deletecartitem")
    fun setDeleteCartItem(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("summary")
    fun setSummary(@Body map: HashMap<String, String>): Call<RestSummaryResponse>

    @POST("order")
    fun setOrderPayment(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("forgotPassword")
    fun setforgotPassword(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getorderdetails")
    fun setgetOrderDetail(@Body map: HashMap<String, String>): Call<RestOrderDetailResponse>

    @GET("searchitem")
    fun setSearch(): Call<ListResponse<SearchItemModel>>

    @POST("favoritelist")
    fun getFavouriteList(@Body map: HashMap<String, String>, @Query("page") strPageNo: String): Call<RestResponse<FoodFavouriteResponseModel>>

    @POST("addfavorite")
    fun setAddFavorite(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("removefavorite")
    fun setRemovefavorite(@Body map: HashMap<String, String>): Call<SingleResponse>

    @GET("promocodelist")
    fun getPromoCodeList(): Call<ListResponse<PromocodeModel>>

    @POST("promocode")
    fun setApplyPromocode(@Body map: HashMap<String, String>): Call<RestResponse<GetPromocodeModel>>

    @POST("cartcount")
    fun getCartCount(@Body map: HashMap<String, String>): Call<CartCountModel>

    @GET("banner")
    fun getBanner(): Call<ListResponse<BannerModel>>

    @GET("restaurantslocation")
    fun getLocation(): Call<RestResponse<LocationModel>>

    @GET("isopenclose")
    fun getCheckStatusRestaurant(): Call<SingleResponse>

    @POST("checkpincode")
    fun setCheckPinCode(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("resendotp")
    fun setResendEmailVerification(@Body map: HashMap<String,String>):Call<SingleResponse>

    @POST("otpverify")
    fun setEmailVerify(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("wallet")
    fun getWallet(@Body map: HashMap<String, String>): Call<ListResponse<WalletModel>>

    @POST("paymenttype")
    fun getPaymentType(@Body map: HashMap<String, String>): Call<PaymentListResponce>

    @POST("ordercancel")
    fun setOrderCancel(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("address")
    fun addAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("updateaddress")
    fun updateAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("deleteaddress")
    fun deleteAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getaddress")
    fun getAddress(@Body map: HashMap<String, String>): Call<ListResopone<AddressResponse>>

    @POST("addmoney")
    fun addMoney(@Body map: HashMap<String, String>): Call<SingleResponse>

    @GET("pincode")
    fun getNeighbourhood(): Call<ListResopone<PinCodeResponse>>

    @GET("checkaddons")
    fun getLoginType(): Call<GetLoginTypeResponseModel>

    @POST("contact")
    fun contactUs(@Body map: HashMap<String, String>): Call<SingleResponse>


    @POST("relateditem")
    fun getRelatedProduct(@Body map: HashMap<String,String>,@Query("page")strPageNo:String):Call<RestResponse<FoodItemResponseModel>>

}