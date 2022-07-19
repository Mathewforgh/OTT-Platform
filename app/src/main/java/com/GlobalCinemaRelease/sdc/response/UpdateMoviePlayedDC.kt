package com.GlobalCinemaRelease.sdc.response

data class UpdateMoviePlayedDC(
    val code: String?, // 503 or success
    val message: String? // You didn't purchased this movie yet
)