package com.example.splmobile.ktor

import com.example.splmobile.response.LixeiraResult
import kotlinx.serialization.json.JsonElement

interface LixeiraApi {
        suspend fun getJsonFromApi(): JsonElement?
}