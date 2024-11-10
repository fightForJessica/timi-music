package com.timi.centre.found.bean


import com.timi.centre.found.bean.BannerObject
import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET

interface BannerService {

    @GET(WebConstant.Found.API_BANNER)
    fun getBannerData(): Call<BannerObject>
}