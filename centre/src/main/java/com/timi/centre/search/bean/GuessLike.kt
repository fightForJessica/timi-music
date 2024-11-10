package com.timi.centre.search.bean

data class GuessLike(
    val code: Int,
    val result: GuessResult
)

data class GuessResult(
    val hots: List<Guess>?
)

data class Guess(
    val first: String,
    val iconType: Int,
    val second: Int,
    val third: Any
)

