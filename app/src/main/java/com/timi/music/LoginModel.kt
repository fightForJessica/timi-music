package com.timi.music


import com.timi.music.bean.Captcha
import com.timi.music.bean.CaptchaService
import com.timi.music.bean.Tourist
import com.timi.music.bean.TouristService
import com.timi.utils.Logger
import com.timi.utils.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel {

    private val TAG = "LoginModel"

    /**
     * 发出获取验证码请求
     */
    fun codeRequest(
        loginVM: LoginViewModel,
        phoneNumber: String
    ){
        val captchaService = ServiceBuilder.create(CaptchaService::class.java)
        captchaService.getCaptchaData(phoneNumber).enqueue(object : Callback<Captcha> {
            override fun onResponse(call: Call<Captcha>, response: Response<Captcha>) {
                response.body()?.apply {
                    Logger.i(TAG, "注册验证码请求:$this")
                    loginVM.captchaCode.value = this.code
                }
            }
            override fun onFailure(call: Call<Captcha>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * 进行游客登陆
     */
    fun touristLoginRequest(loginVM: LoginViewModel){
        val touristService = ServiceBuilder.create(TouristService::class.java)
        touristService.getTouristData().enqueue(object : Callback<Tourist>{
            override fun onResponse(call: Call<Tourist>, response: Response<Tourist>) {
                response.body()?.apply {
                    Logger.i(TAG, "游客登陆请求:$this")
                    loginVM.touristData.value = this
                }
            }
            override fun onFailure(call: Call<Tourist>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}