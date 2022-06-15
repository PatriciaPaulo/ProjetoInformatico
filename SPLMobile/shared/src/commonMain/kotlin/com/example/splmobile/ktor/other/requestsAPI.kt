package com.example.splmobile.ktor.other

import io.ktor.client.statement.*
import kotlinx.serialization.json.JsonElement


interface requestsAPI {
    suspend fun getJsonFromApi(place :String): String
}