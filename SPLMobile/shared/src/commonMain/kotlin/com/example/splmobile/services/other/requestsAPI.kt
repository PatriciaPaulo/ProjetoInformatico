package com.example.splmobile.services.other


interface requestsAPI {
    suspend fun getJsonFromApi(place :String): String
}