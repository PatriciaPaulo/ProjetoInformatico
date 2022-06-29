package com.example.splmobile.dtos.locaisLixo

import com.example.splmobile.dtos.lixeiraEvento.LocalLixoEventoSer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalLixoSer(
    val id: Long,
    val nome: String,
    val criador: Long,
    val latitude: String,
    val longitude: String,
    val estado: String,
    val aprovado: Boolean,
    val foto: String?,
    val eventos: List<LocalLixoEventoSer>
)