package com.timi.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object BitmapRequest {

	/**
	 * 通过图片的Url获取相应的bitmap资源
	 * @param url 需要加载的图片Url地址
	 * @param onSuccess 请求成功后的回调
	 */
	fun getImageBitmap(
		url: String,
		onSuccess: (Bitmap) -> Unit
	){
		CoroutineScope(Dispatchers.Main).launch {
			withContext(Dispatchers.IO){
				var connection: HttpURLConnection? = null
				try {
					connection = URL(url).openConnection() as HttpURLConnection
					connection.apply {
						requestMethod = "GET"
						readTimeout = 8000
						onSuccess(BitmapFactory.decodeStream(inputStream))
					}
				}catch (e: Exception){
					e.printStackTrace()
				}finally {
					connection?.disconnect()
				}
			}
		}
	}

}