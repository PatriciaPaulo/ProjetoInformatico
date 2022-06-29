package com.example.splmobile.services.locaisLixo

import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.locaisLixo.LocaisLixoResponse
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger


class LocalLixoApiImpl(private val log: KermitLogger, engine: HttpClientEngine) : LocalLixoApi {
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : KtorLogger {
                override fun log(message: String) {
                    log.v { message + "API IMP MESSAGE"}
                }
            }

            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        /*install(Auth) {
            bearer {
                // Load tokens ...
                refreshTokens { // this: RefreshTokensParams
                    // Refresh tokens and return them as the 'BearerTokens' instance
                    BearerTokens("def456", "xyz111")
                }
            }
        }*/

    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getLocaisLixo(): LocaisLixoResponse {
        log.d { "Fetching lixeiras from network" }
        return client.get {
            contentType(ContentType.Application.Json)
            url("api/lixeiras")
        }.body() as LocaisLixoResponse
    }



    override suspend fun postLocalLixo(localLixo: LocalLixo, token: String): RequestMessageResponse {
        log.d { "post new Local Lixo" }
        try{
            return client.post {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(LocalLixoSer(localLixo.id, localLixo.nome, localLixo.criador,localLixo.latitude,localLixo.longitude,localLixo.estado,localLixo.aprovado,localLixo.foto,
                    emptyList()))
                url("api/lixeiras")
            }.body() as RequestMessageResponse
        }
        catch (e: Exception){
            println("Error: ${e.message}")
            return RequestMessageResponse(e.message!!)
        }

    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }
}
