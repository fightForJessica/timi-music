package com.timi.centre.search.bean

import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET

interface GuessLikeService {

    @GET(WebConstant.Search.API_HOT_RECOMMAND)
    fun getGuessLikeData(): Call<GuessLike>
}