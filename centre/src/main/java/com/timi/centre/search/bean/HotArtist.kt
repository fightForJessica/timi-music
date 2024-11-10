package com.timi.centre.search.bean

data class HotArtist(
    val artists: List<Artist>?,
    val code: Int,
    val more: Boolean
)

data class Artist(
    val alias: List<String>,
    val fansCount: Int,
    val id: Int,
    val img1v1Url: String,
    val name: String,
    val picUrl: String,
)