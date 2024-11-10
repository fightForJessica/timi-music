package com.timi.centre.playlistsquare.bean

data class PlaylistCategory(
    val all: All,
    val categories: Categories,
    val code: Int,
    val sub: List<Sub>
)

data class All(
    val activity: Boolean,
    val category: Int,
    val hot: Boolean,
    val name: String,
    val resourceCount: Int,
)

data class Categories(
    val `0`: String,
    val `1`: String,
    val `2`: String,
    val `3`: String,
    val `4`: String
)

data class Sub(
    val activity: Boolean,
    val category: Int,
    val hot: Boolean,
    val name: String,
    val resourceCount: Int,
)