package com.example.splmobile.dtos.eventos

import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import kotlinx.serialization.Serializable


@Serializable
data class EventosResponse(
    val data: List<EventoSer>,
    val status: String,
    val message: String,
)