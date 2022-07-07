package com.example.splmobile.dtos.myInfo

import kotlinx.serialization.Serializable


@Serializable
data class UtilizadorSer(
    val id: Long,
    val username: String,
    val email: String,
    val name: String,

)