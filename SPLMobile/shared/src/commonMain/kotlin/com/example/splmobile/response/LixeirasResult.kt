package com.example.splmobile.response
import kotlinx.serialization.Serializable

@Serializable
class LixeirasResult (
    val message: Map<String, List<String>>,
    var status: String
    )