package com.timi.centre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    /**
     * Fragment的TAG
     */
    abstract val TAG: String

    /**
     *  加载Fragment布局
     */
    abstract fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    /**
     *  在布局引入后，进行初始化操作
     */
    abstract fun viewCreate(view: View, savedInstanceState: Bundle?)

    /**
     *  观察 ViewModel 中数据的变化
     */
    abstract fun observeChange()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        createView(inflater, container, savedInstanceState)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewCreate(view, savedInstanceState)
        observeChange()
    }

}