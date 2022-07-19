package com.GlobalCinemaRelease.sdc.response

data class ProfilePgDetailsDC(
    val code: Int?, // 201
    val giftedHistory: List<GiftedHistory?>,
    val paymentHistory: List<PaymentHistory?>?,
    val preferedLanguages: List<PreferedLanguage?>
)