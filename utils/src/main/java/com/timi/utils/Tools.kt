package com.timi.utils

import android.graphics.Bitmap
import android.graphics.Matrix


private var matrix: Matrix? = null


/**
 * 通过Matrix对Bitmap进行压缩
 * @param sx 对水平方向的压缩量
 * @param sy 对垂直方向的压缩量
 * @return 返回压缩完成的Bitmap
 */
fun getCompressBitmap(sx: Float, sy: Float, bitmap: Bitmap): Bitmap{
	if (matrix == null) matrix = Matrix()
	matrix!!.setScale(sx, sy)
	return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}