package com.timi.centre.search.bean

data class HotSearch(
    val code: Int,
    val data: List<HotSearchData>?,
    val message: String
)

data class HotSearchData(
    val iconUrl: String?,
    val score: Int,
    val searchWord: String,
    val content: String
)