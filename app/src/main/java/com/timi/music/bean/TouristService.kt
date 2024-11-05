package com.timi.music.bean

import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET

interface TouristService {

    @GET(WebConstant.Login.API_TOURIST)
    fun getTouristData(): Call<Tourist>
}