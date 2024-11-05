package com.timi.utils

import okhttp3.Interceptor
import okhttp3.Response

internal class AddCookieInterceptor(
    private val cookie: String
) : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("Cookie", cookie)
        return chain.proceed(builder.build())
    }
}
