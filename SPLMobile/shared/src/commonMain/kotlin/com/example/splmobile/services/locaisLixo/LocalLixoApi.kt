package com.example.splmobile.services.locaisLixo

import com.example.splmobile.database.LocalLixo
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.locaisLixo.LocaisLixoResponse
import io.ktor.client.statement.*

interface LocalLixoApi {
        suspend fun getLocaisLixo(): LocaisLixoResponse
        suspend fun postLocalLixo(localLixo: LocalLixo, token: String): RequestMessageResponse
        suspend fun patchLocalLixoEstado(localLixo: LocalLixo, s: String): RequestMessageResponse
}