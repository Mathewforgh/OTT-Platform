package com.GlobalCinemaRelease.sdc.response

data class SendOtp(
    val code: Int?,
    val countryCode: String?,
    val message: String?
)