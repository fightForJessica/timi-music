package com.timi.centre.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.timi.centre.R

/**
 * 自定义 Toolbar 搜索栏
 */
class SearchToolbar : Toolbar{

    var toolbar: Toolbar
    private var constantLayout: ConstraintLayout
    private var iv_searchBox: ImageView    //搜索框
    private var iv_searchIcon: ImageView   //放大镜图片
    var edt_inputBox: EditText
    var tv_search: TextView

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_toolbar_search, this, true)
        toolbar = findViewById(R.id.toolbar_Search)
        constantLayout = findViewById(R.id.constraintLayout_Search)
        iv_searchBox = findViewById(R.id.iv_Search_searchBox)
        iv_searchIcon = findViewById(R.id.iv_Search_searchIcon)
        edt_inputBox = findViewById(R.id.edt_Search_inputBox)
        tv_search = findViewById(R.id.tv_Search_search)
        iv_searchIcon.setBackgroundResource(R.drawable.ic_search)
    }

    /**
     * 设置"搜索"TextView点击事件
     */
    fun setSearchTextOnClickListener(onClickListener: OnClickListener){
        tv_search.setOnClickListener(onClickListener)
    }

    /**
     * 设置该布局与搜索TextView的点击事件
     */
    fun setLayoutOnClickListener(onClickListener: OnClickListener){
        constantLayout.setOnClickListener(onClickListener)
        tv_search.setOnClickListener(onClickListener)
    }

    /**
     * 进行部分初始化toolbar
     * @param activity 当前组件所在的Activity
     * @param block 更多的初始化内容
     */
    fun initToolbar(
        activity: AppCompatActivity,
        block: ((activity: AppCompatActivity) -> Unit)? = null){
        activity.apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }
        block?.invoke(activity)
    }
}