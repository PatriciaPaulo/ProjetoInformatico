package com.example.splmobile.dtos.files

import kotlinx.serialization.Serializable

@Serializable
data class FileSerializable (
    var path : String,
    var name : String
)