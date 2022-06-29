package com.example.splmobile.dtos.locaisLixo

import kotlinx.serialization.Serializable


@Serializable
data class LocaisLixoResponse(
    val locaisLixo: List<LocalLixoSer>,
    val message: String
)