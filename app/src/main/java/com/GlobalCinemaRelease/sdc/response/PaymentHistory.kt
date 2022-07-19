package com.GlobalCinemaRelease.sdc.response

data class PaymentHistory(
    val Payment: String?, // ₹199/1day
    val movieDetails: String?, // Tamil | Action | Comedy | Thriller | 02:54 hrs
    val movieName: String?, // Test 1
    val movieToken: String?, // 8227423935
    val posterLink: String?, // https://d2o7hjcj8mc8gg.cloudfront.net/Vikram.jpeg
    val purchasedDate: String?, // 15 Jun 2022
    val rentToken: String?, // 1974478264
    val isRated: Boolean?, //false
    val rating: String? // 2
)