package com.example.splmobile.dtos.myInfo

import com.example.splmobile.dtos.eventos.EventoSer
import kotlinx.serialization.Serializable


@Serializable
data class UtilizadorResponse(
    val data: UtilizadorSer,
    val status: String,
    val message: String,
)