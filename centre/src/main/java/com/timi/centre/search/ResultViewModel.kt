package com.timi.centre.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timi.centre.search.bean.Search

class ResultViewModel: ViewModel() {

    private val resultModel: ResultModel by lazy { ResultModel() }

    val searchData = MutableLiveData<Search?>()                 //搜索结果

    init {
        searchData.value = null
    }

    /**
     * 发出搜索请求
     * @param content 搜索内容
     */
    fun searchRequest(content: String, offset: Int){
        resultModel.searchRequest(this, content, offset)
    }

}