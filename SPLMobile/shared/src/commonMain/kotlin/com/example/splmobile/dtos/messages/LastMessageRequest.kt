package com.example.splmobile.dtos.messages

import kotlinx.serialization.Serializable


@Serializable
data class LastMessageRequest (
    val IDs:List<Long>,
)