package com.timi.centre.found.bean


import com.timi.centre.found.bean.RecommandPlay
import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET

interface RecommandPlayService {

    @GET(WebConstant.Found.API_RECOMMANDPLAY)
    fun getRecommandPlayData(): Call<RecommandPlay>

}