package com.example.splmobile.objects.messages

import kotlinx.serialization.Serializable


@Serializable
data class LastMessageRequest (
    val IDs:List<Long>,
)