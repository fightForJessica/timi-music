package com.timi.centre.search.bean

data class Search(
    val code: Int,
    val result: Result
)

data class Result(
    val hasMore: Boolean,
    val songCount: Int,
    val songs: List<Song>?
)

data class Song(
    val album: Album,
    val artists: List<ArtistX>,
    val duration: Int,
    val id: Long,
    val mvid: Int,
    val name: String,
)

data class Album(
    val artist: ArtistX,
    val id: Long,
    val name: String,
    val picId: Long,
    val size: Int,
)

data class ArtistX(
    val id: Int,
    val img1v1: Int,
    val img1v1Url: String,
    val name: String,
)