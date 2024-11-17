package com.timi.player.store.bean

data class Lyric(
    val code: Int,
    val lrc: Lrc,
)

data class Lrc(
    val lyric: String,
)

data class ProcessedLyric(
    val word: String,
    val startTime: Int
)