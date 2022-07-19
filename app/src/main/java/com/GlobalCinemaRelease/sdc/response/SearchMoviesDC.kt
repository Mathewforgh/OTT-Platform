package com.GlobalCinemaRelease.sdc.response

data class SearchMoviesDC(
    val code: Int?, // 201
    val `data`: List<SearchMovieList>?
)