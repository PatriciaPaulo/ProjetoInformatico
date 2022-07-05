package com.example.splmobile.dtos.locaisLixo

import kotlinx.serialization.Serializable


@Serializable
data class LocaisLixoResponse(
    val data: List<LocalLixoSer>,
    val status: String,
    val message: String,
)