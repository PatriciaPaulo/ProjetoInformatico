package com.example.splmobile.dtos.myInfo

import kotlinx.serialization.Serializable


@Serializable
data class UserSerializable(
    val id: Long,
    var username: String,
    var email: String,
    var name: String,
    var icon: String?

    )