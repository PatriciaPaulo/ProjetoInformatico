package com.example.splmobile.objects.myInfo

import kotlinx.serialization.Serializable


@Serializable
data class UserSerializable(
    val id: Long,
    var username: String,
    var email: String,
    var name: String,
    var icon: String?,
    var admin :Boolean?

    )