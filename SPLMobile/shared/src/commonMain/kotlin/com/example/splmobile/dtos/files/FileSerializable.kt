package com.example.splmobile.dtos.files

import kotlinx.serialization.Serializable
import

@Serializable
data class FileSerializable (
    var parentID: File,
    var path: String
)