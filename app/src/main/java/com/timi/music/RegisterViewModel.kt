package com.timi.music

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.music.RegisterModel

class RegisterViewModel: ViewModel() {
    private val registerModel: RegisterModel by lazy { RegisterModel() }

    val captchaCode = MutableLiveData<Int>()        //验证码发送返回的结果

    init {
        captchaCode.value = 0
    }

    /**
     * 发出获取验证码请求
     */
    fun codeRequest(phoneNumber: String){
        registerModel.codeRequest(this, phoneNumber)
    }
}