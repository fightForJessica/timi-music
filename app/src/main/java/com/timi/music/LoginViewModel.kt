package com.timi.music

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.music.bean.Tourist

class LoginViewModel: ViewModel() {

    private val TAG = "LoginViewModel"
    private val loginModel: LoginModel by lazy { LoginModel() }

    val captchaCode = MutableLiveData<Int>()        //验证码发送返回结果
    val touristData = MutableLiveData<Tourist?>()        //游客登陆返回结果

    init {
        captchaCode.value = 0
        touristData.value = null
    }

    /**
     * 发出获取验证码请求
     */
    fun codeRequest(phoneNumber: String){
        loginModel.codeRequest(this, phoneNumber)
    }

    fun touristLoginRequest(){
        loginModel.touristLoginRequest(this)
    }
}