package com.timi.utils


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {

    private val myClient = OkHttpClient.Builder()       //自定义超时client
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)

    private val baseRetrofitBuilder = Retrofit.Builder()
        .baseUrl(WebConstant.currentUrl)
        .addConverterFactory(GsonConverterFactory.create())

    /**
     * 创建Service对象，供以进行网络请求
     * @param serviceClass 提供反射的service接口
     * @param cookie 所携带的cookie，默认为空
     */
    fun <T> create(
        serviceClass: Class<T>,
        cookie: String? = null,
    ): T {
        val retrofit = baseRetrofitBuilder
        return if (cookie == null){
            retrofit.client(myClient.build()).build().create(serviceClass)
        } else {
            retrofit
                .client(myClient.addInterceptor(AddCookieInterceptor(cookie)).build())
                .build()
                .create(serviceClass)
        }
    }

}

