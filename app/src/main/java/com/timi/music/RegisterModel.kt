package com.timi.music

import android.util.Log
import com.example.musicplus.bean.Captcha
import com.example.musicplus.bean.CaptchaService
import com.example.utils.web.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterModel {

    private val TAG = "RegisterModel"

    /**
     * 发出获取验证码请求
     */
    fun codeRequest(
        registerVM: RegisterViewModel,
        phoneNumber: String
    ){
        val captchaService = ServiceBuilder.create(CaptchaService::class.java)
        captchaService.getCaptchaData(phoneNumber).enqueue(object : Callback<Captcha>{
            override fun onFailure(call: Call<Captcha>, t: Throwable) {
                t.printStackTrace()
            }
            override fun onResponse(call: Call<Captcha>, response: Response<Captcha>) {
                response.body()?.apply {
                    Log.i(TAG, "注册验证码请求:${this}")
                    registerVM.captchaCode.value = this.code
                }
            }
        })
    }
}