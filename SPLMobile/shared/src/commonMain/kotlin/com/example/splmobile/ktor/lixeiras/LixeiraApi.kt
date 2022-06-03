package com.example.splmobile.ktor.lixeiras

import kotlinx.serialization.json.JsonElement

interface LixeiraApi {
        suspend fun getJsonFromApi(): JsonElement?
}