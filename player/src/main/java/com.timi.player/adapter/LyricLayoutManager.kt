package com.timi.player.adapter

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class LyricLayoutManager : LinearLayoutManager {

	constructor(context: Context): super(context)
	constructor(context: Context, orientation: Int, reverseLayout: Boolean): super(context, orientation, reverseLayout)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

	override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
		//调整滚动逻辑
		val smoothScroller = LyricSmoothScroller(recyclerView.context) as RecyclerView.SmoothScroller
		smoothScroller.targetPosition = position
		startSmoothScroll(smoothScroller)
	}

	companion object{
		private class LyricSmoothScroller(context: Context) : LinearSmoothScroller(context){

			override fun calculateDtToFit(
				viewStart: Int,
				viewEnd: Int,
				boxStart: Int,
				boxEnd: Int,
				snapPreference: Int
			): Int =	//移动到距离屏幕顶二分之一的位置
				(boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2))

			//调整滚动速度，数字越小滚动越快
			override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float =
				100f / displayMetrics.densityDpi
		}
	}

}