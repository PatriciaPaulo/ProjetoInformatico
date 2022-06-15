package com.example.splmobile.ktor.auth

import com.example.splmobile.database.LocalLixo
import kotlinx.serialization.json.JsonElement

interface authAPI {
    suspend fun postLogin(username: String,password:String): String
}