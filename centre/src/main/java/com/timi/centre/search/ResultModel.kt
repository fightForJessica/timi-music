package com.timi.centre.search

import com.timi.centre.search.bean.Search
import com.timi.centre.search.bean.SearchService
import com.timi.utils.Logger
import com.timi.utils.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultModel {

    private val TAG = "ResultModel"

    /**
     * 发出搜索数据请求
     * @param content 搜索内容
     * @param offset 偏移量(页数)
     */
    fun searchRequest(resultVM: ResultViewModel, content: String, offset: Int){
        ServiceBuilder.create(SearchService::class.java)
            .getSearchData(content, offset).enqueue(object : Callback<Search> {
                override fun onResponse(call: Call<Search>, response: Response<Search>) {
                    response.body()?.apply {
                        Logger.i(TAG, "搜索请求:$this")
                        resultVM.searchData.value = this
                    }
                }
                override fun onFailure(call: Call<Search>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

}