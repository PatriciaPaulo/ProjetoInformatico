package com.example.splmobile.objects.files

import com.soywiz.korio.file.VfsFile
import kotlinx.serialization.Serializable
//import

@Serializable
data class FileSerializable (
    var path : String,
    var name : String
)