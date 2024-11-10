package com.timi.centre.found.bean

data class RecommandPlay(
    val category: Int,
    val code: Int,
    val hasTaste: Boolean,
    val result: List<RecommandPlayResult>?
)

data class RecommandPlayResult(
    val alg: String,
    val highQuality: Boolean,
    val id: Long,
    val name: String,
    val picUrl: String,
)