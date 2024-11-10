package com.timi.centre.search.bean

import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    /**
     * @param keywords 搜索关键字
     * @param offset 偏移量(页数)
     * @param limit 搜索内容数量
     */
    @GET(WebConstant.Search.API_SEARCH)
    fun getSearchData(
        @Query("keywords") keywords: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = 15
    ): Call<Search>
}