package com.timi.music.bean


import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CaptchaService {

    @GET(WebConstant.Captcha.API_CAPTCHA)
    fun getCaptchaData(@Query("phone") phone: String): Call<Captcha>
}