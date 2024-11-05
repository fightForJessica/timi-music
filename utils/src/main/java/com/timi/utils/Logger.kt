package com.timi.utils

import android.util.Log

/**
 * 日志打印工具
 */
object Logger {

    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5

    const val currentLevel = ERROR

    fun v(tag: String, msg: String){
        if (currentLevel >= VERBOSE) Log.v(tag, msg)
    }

    fun d(tag: String, msg: String){
        if (currentLevel >= DEBUG) Log.d(tag, msg)
    }

    fun i(tag: String, msg: String){
        if (currentLevel >= INFO) Log.i(tag, msg)
    }

    fun w(tag: String, msg: String){
        if (currentLevel >= WARN) Log.w(tag, msg)
    }

    fun e(tag: String, msg: String){
        if (currentLevel >= ERROR) Log.e(tag, msg)
    }
}