package com.timi.centre.playlistsquare.bean

data class SquarePlaylistDetail(
    val code: Int,
    val more: Boolean,
    val playlists: List<Playlists>,
    val total: Int
)

data class Playlists(
    val commentCount: Int,
    val copywriter: String,
    val coverImgUrl: String,
    val coverStatus: Int,
    val createTime: Long,
    val creator: Creator,
    val description: String,
    val id: Long,
    val name: String,
    val playCount: Int,
    val shareCount: Int,
    val subscribed: Boolean,
    val subscribedCount: Int,
    val subscribers: List<Subscriber>,
    val tag: String,
    val tags: List<String>,
    val updateTime: Long,
    val userId: Long
)

data class Creator(
    val avatarUrl: String,
    val backgroundUrl: String,
    val description: String,
    val detailDescription: String,
    val expertTags: List<String>,
    val nickname: String,
    val signature: String,
    val userId: Long,
    val vipType: Int
)

data class Subscriber(
    val avatarUrl: String,
    val backgroundUrl: String,
    val description: String,
    val detailDescription: String,
    val nickname: String,
    val userId: Long,
    val vipType: Int
)