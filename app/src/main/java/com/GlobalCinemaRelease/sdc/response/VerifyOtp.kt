package com.GlobalCinemaRelease.sdc.response

data class VerifyOtp(
    val code: Int?,
    val message: String?,
    val newUser: Boolean?,
    val userToken: String?
)