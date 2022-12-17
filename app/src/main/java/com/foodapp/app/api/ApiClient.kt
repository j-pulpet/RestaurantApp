package com.foodapp.app.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    val BASE_URL="YOUR_DOMAIN_URL/"

    val APP_URL= BASE_URL+"api/"

    val PrivicyPolicy=BASE_URL+"privacypolicy"

    val aboutus=BASE_URL+"aboutus"

    val termscondition=BASE_URL+"termscondition"

    var TIMEOUT: Long = 60 * 2 * 1.toLong()
    val getClient: ApiInterface get() {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient=OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(APP_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ApiInterface::class.java)
    }
}