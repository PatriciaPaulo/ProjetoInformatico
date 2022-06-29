package com.example.splmobile.dtos.lixeiraEvento

import kotlinx.serialization.Serializable


@Serializable
data class LocalLixoEventoSer(
    val id: Long,
    val lixeiraID: Long,
    val eventoID: Long,

)