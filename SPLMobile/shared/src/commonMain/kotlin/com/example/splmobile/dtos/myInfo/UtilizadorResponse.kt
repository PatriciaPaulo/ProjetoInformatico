package com.example.splmobile.dtos.myInfo

import kotlinx.serialization.Serializable

@Serializable
data class UtilizadorResponse(
    val data: UtilizadorSer,
    val status: String,
    val message: String,
)

data class EmailCheckResponse(
    val status: String,
)