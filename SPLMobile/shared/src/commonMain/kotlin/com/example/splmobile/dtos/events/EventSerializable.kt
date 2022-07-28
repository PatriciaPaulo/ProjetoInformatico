package com.example.splmobile.dtos.events

import kotlinx.serialization.Serializable


@Serializable
data class EventSerializable(
    val id: Long,
    val name: String,
    val latitude: String,
    val longitude: String,
    val status: String,
    val duration: String,
    val startDate: String,
    val description: String,
    val accessibility: String,
    val restrictions: String,
    val quantity: String,
    val observations: String?
)