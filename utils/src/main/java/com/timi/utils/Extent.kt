package com.timi.utils

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * 取消虚拟键盘
 */
fun View.hideKeyboard(){
    val inputMethodManager = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}


//标志上一次点击的时刻
private var lastClickTime: Long = 0
/**
 * 判断是否短时间内进行了快速点击，用于过滤多余的点击
 * @param mills 拦截相隔时长内的多次点击
 * @return 该次点击是否有效
 */
fun View.isClickEffective(mills: Int = 500): Boolean{
    val currentClickTime = System.currentTimeMillis()
    val clickEffective = currentClickTime - lastClickTime >= mills
    lastClickTime = currentClickTime
    if (!clickEffective)
        Logger.w("ViewClicked", "This quick click was intercepted.\nThe click position was $this")
    return clickEffective
}


private var toast: Toast? = null
/**
 * 显示Toast,防止Toast多次连续显示一直提示而不消失
 * @param text Toast展示的文本
 * @param time 展示时长，默认为[Toast.LENGTH_SHORT]
 */
fun Fragment.makeToast(text: String, time: Int = Toast.LENGTH_SHORT){
    if (toast == null){
        toast = Toast.makeText(requireContext(), text, time)
    }else{
        toast!!.setText(text)
    }
    toast!!.show()
}
