package com.GlobalCinemaRelease.sdc.response

data class CodeResponse(
    val code : Int?,
    val message : String?,
    val data : List<Data>?
)
