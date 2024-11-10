package com.timi.centre.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.timi.centre.R

class ButtonMore : ConstraintLayout {

	constructor(context: Context): this(context, null)
	constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

	private var constraintLayout: ConstraintLayout
	private var iv_icon: ImageView
	private var tv_count: TextView
	private var btn: Button

	init {
		LayoutInflater.from(context).inflate(R.layout.widget_button_more, this, true)
		constraintLayout = findViewById(R.id.constraintLayout_ButtonMore)
		iv_icon = findViewById(R.id.iv_ButtonMore_icon)
		tv_count = findViewById(R.id.tv_ButtonMore_count)
		btn = findViewById(R.id.btn_ButtonMore)
		btn.setBackgroundResource(R.drawable.bg_shape_button_playlist)
		btn.background.alpha = 70
	}

	/**
	 * 设置该组件中图标
	 */
	fun setIcon(resId: Int){
		iv_icon.setImageResource(resId)
	}

	/**
	 * 设置展示数量前的默认文本
	 */
	fun setDefaultText(text: String){
		tv_count.text = text
	}

	/**
	 * 设置该组件中的展示数量
	 */
	fun setCount(count: Int){
		tv_count.text = if (count >= 10000) "${count / 10000}.${count / 1000 % 10}w" else count.toString()
	}

	override fun setBackgroundColor(color: Int){
		constraintLayout.setBackgroundColor(color)
	}
}