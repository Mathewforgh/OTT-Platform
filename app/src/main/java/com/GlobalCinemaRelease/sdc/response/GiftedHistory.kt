package com.GlobalCinemaRelease.sdc.response

data class GiftedHistory(
    val rentToken: String?,
    val movieToken: String?,
    val posterLink: String?,
    val movieName: String?,
    val movieDetails: String?,
    val Payment: String?,
    val purchasedDate: String?,
    val redeemedBy: String?,
    val giftId: String?,
    val isRated: Boolean?
)
