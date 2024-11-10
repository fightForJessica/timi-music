package com.timi.centre.search.bean

import com.timi.utils.WebConstant
import retrofit2.Call
import retrofit2.http.GET

interface HotSearchService {

    @GET(WebConstant.Search.API_HOT_SEARCH)
    fun getHotSearchData(): Call<HotSearch>
}