package com.example.splmobile.dtos.files

import com.soywiz.korio.file.VfsFile
import kotlinx.serialization.Serializable

@Serializable
data class FileSerializable (
    var path : String,
    var name : String
)