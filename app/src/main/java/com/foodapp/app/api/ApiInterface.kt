package com.foodapp.app.api

import com.foodapp.app.model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    //Login Api 1
    @POST("login")
    fun getLogin(@Body map: HashMap<String, String>): Call<RestResponse<LoginModel>>

    //Registration Api 2
    @POST("register")
    fun setRegistration(@Body map: HashMap<String, String>): Call<RestResponse<RegistrationModel>>

    //Profile Api 3
    @POST("getprofile")
    fun getProfile(@Body map: HashMap<String, String>): Call<RestResponse<ProfileModel>>

    //EditProfile Api 4
    @Multipart
    @POST("editprofile")
    fun setProfile(@Part("user_id") userId: RequestBody,@Part("name") name: RequestBody, @Part profileimage: MultipartBody.Part?): Call<SingleResponse>

    //Chnage Password  Api 5
    @POST("changepassword")
    fun setChangePassword(@Body map: HashMap<String, String>):Call<SingleResponse>

    //Category  Api 6
    @GET("category")
    fun getFoodCategory():Call<ListResponse<FoodCategoryModel>>

    //Item  Api 7
    @POST("item")
    fun getFoodItem(@Body map: HashMap<String, String>,@Query("page")strPageNo:String):Call<RestResponse<FoodItemResponseModel>>

    //Item  Api 9
    @POST("orderhistory")
    fun getOrderHistory(@Body map: HashMap<String, String>):Call<ListResponse<OrderHistoryModel>>

    //Item  Api 10
    @GET("rattinglist")
    fun getRatting():Call<ListResponse<RattingModel>>

    //Getcart  Api 11
    @POST("getcart")
    fun getCartItem(@Body map: HashMap<String, String>):Call<ListResponse<CartItemModel>>

    //Ratting  Api 12
    @POST("ratting")
    fun setRatting(@Body map: HashMap<String, String>):Call<SingleResponse>

    //ItemDetail  Api 13
    @POST("itemdetails")
    fun setItemDetail(@Body map: HashMap<String, String>):Call<RestResponse<ItemDetailModel>>

    //cart  Api 14
    @POST("cart")
    fun setAddToCart(@Body map: HashMap<String, String>):Call<SingleResponse>

    //QtyUpdate Api 15
    @POST("qtyupdate")
    fun setQtyUpdate(@Body map: HashMap<String, String>):Call<SingleResponse>

    //DeleteCartItem Api 16
    @POST("deletecartitem")
    fun setDeleteCartItem(@Body map: HashMap<String, String>):Call<SingleResponse>

    //Summary Api 17
    @POST("summary")
    fun setSummary(@Body map: HashMap<String, String>):Call<RestSummaryResponse>

    //OrderPayment Api 18
    @POST("order")
    fun setOrderPayment(@Body map: HashMap<String, String>):Call<SingleResponse>

    //forgotPassword Api 19
    @POST("forgotPassword")
    fun setforgotPassword(@Body map: HashMap<String, String>):Call<SingleResponse>

    //OrderDetail Api 20
    @POST("getorderdetails")
    fun setgetOrderDetail(@Body map: HashMap<String, String>):Call<RestOrderDetailResponse>

    //Search Api 21
    @POST("searchitem")
    fun setSearch(@Body map: HashMap<String, String>,@Query("page")strPageNo:String):Call<RestResponse<FoodItemResponseModel>>

    //PromoCode Api 23
    @POST("favoritelist")
    fun getFavouriteList(@Body map: HashMap<String, String>,@Query("page")strPageNo:String):Call<RestResponse<FoodFavouriteResponseModel>>

    //AddFavorite Api 24
    @POST("addfavorite")
    fun setAddFavorite(@Body map: HashMap<String, String>):Call<SingleResponse>

    //Removefavorite Api 25
    @POST("removefavorite")
    fun setRemovefavorite(@Body map: HashMap<String, String>):Call<SingleResponse>

    //PromoCode Api 26
    @GET("promocodelist")
    fun getPromoCodeList():Call<ListResponse<PromocodeModel>>

    //ApplyPromocode Api 27
    @POST("promocode")
    fun setApplyPromocode(@Body map: HashMap<String, String>):Call<RestResponse<GetPromocodeModel>>

    //ApplyPromocode Api 27
    @POST("cartcount")
    fun getCartCount(@Body map: HashMap<String, String>):Call<CartCountModel>

    //ApplyPromocode Api 28
    @GET("banner")
    fun getBanner():Call<ListResponse<BannerModel>>

    //LocationApi 29
    @GET("restaurantslocation")
    fun getLocation():Call<RestResponse<LocationModel>>

    //check Status Api 30
    @GET("isopenclose")
    fun getCheckStatusRestaurant():Call<SingleResponse>

    //Checkpincode Api 31
    @POST("checkpincode")
    fun setCheckPinCode(@Body map: HashMap<String,String>):Call<SingleResponse>

    //Checkpincode Api 32
    @POST("resendemailverification")
    fun setResendEmailVerification(@Body map: HashMap<String,String>):Call<SingleResponse>

    //getChatList Api 33
    @POST("emailverify")
    fun setEmailVerify(@Body map: HashMap<String, String>): Call<JsonObject>

    //Wallet Api 34
    @POST("wallet")
    fun getWallet(@Body map: HashMap<String, String>):Call<ListResponse<WalletModel>>

    //PaymentType Api 35
    @POST("paymenttype")
    fun getPaymentType(@Body map: HashMap<String, String>):Call<PaymentListResponce>

    //OrderCancel Api 36
    @POST("ordercancel")
    fun setOrderCancel(@Body map: HashMap<String, String>):Call<SingleResponse>

}