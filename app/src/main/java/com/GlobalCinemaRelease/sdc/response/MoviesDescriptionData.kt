package com.GlobalCinemaRelease.sdc.response

data class MoviesDescriptionData(
    val code: Int?,
    val movieDetails: MovieDetails?,
    val searchTitle: String?,
    val suggestions: List<Suggestion?>
)