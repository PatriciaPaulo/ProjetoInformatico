package com.example.splmobile.dtos.garbageTypes

import kotlinx.serialization.Serializable


@Serializable
data class GarbageTypesResponse(
    val data: List<GarbageTypeDTO>,
    val message: String,
)