package com.example.splmobile.services.locaisLixo

import com.example.splmobile.database.LocalLixo
import kotlinx.serialization.json.JsonElement

interface LocalLixoApi {
        suspend fun getLocaisLixoJson(): JsonElement?
        suspend fun postLocalLixo(localLixo: LocalLixo): Any
}