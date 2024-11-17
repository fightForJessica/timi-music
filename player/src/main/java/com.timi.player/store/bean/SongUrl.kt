package com.timi.player.store.bean

data class SongUrl(
    val code: Int,
    val data: List<Data>
)

data class Data(
    val canExtend: Boolean,
    val code: Int,
    val encodeType: String,
    val expi: Int,
    val flag: Int,
    val gain: Double,
    val id: Int,
    val level: String,
    val payed: Int,
    val size: Int,
    val time: Int,
    val type: String,
    val url: String,
)


