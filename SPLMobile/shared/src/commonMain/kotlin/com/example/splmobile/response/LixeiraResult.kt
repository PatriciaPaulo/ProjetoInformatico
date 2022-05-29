package com.example.splmobile.response
import kotlinx.serialization.Serializable

@Serializable
class LixeiraResult (
    val message: Map<String, List<String>>,
    var status: String
    )