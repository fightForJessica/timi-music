package com.timi.centre.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.timi.centre.R

class SidewaysLayout: ConstraintLayout {

    private var constrantLayout: ConstraintLayout
    private var btn_exitLogin: Button

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_constrantlayout_sideway, this, true)
        constrantLayout = findViewById(R.id.constraintLayout_SidewaysLayout)
        btn_exitLogin = findViewById(R.id.btn_SidewaysLayout_exitLogin)
    }

    fun setExitButton(onClickListener: OnClickListener){
        btn_exitLogin.setOnClickListener(onClickListener)
    }

}