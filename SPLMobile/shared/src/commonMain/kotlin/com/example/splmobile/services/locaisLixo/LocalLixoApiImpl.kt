package com.example.splmobile.services.locaisLixo

import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.database.LocalLixo

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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

    override suspend fun getLocaisLixoJson(): JsonElement {
        log.d { "Fetching lixeiras from network" }
        return client.get {
            url("api/lixeiras")
        }.body()
    }

    override suspend fun postLocalLixo(localLixo: LocalLixo): Any {
        log.d { "post new Local Lixo" }
        return client.post {
            headers {
                append(HttpHeaders.Authorization, "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXIiLCJleHAiOjE2NTUzMDA5OTh9.jZhWcULPzMb05q-CTemtYntYM2dYxfMbuAc2hVzT0zc")
            }
            contentType(ContentType.Application.Json)
            setBody(LocalLixoSer(localLixo.id, localLixo.nome, "user",localLixo.longitude,localLixo.latitude,localLixo.estado,localLixo.aprovado,localLixo.foto))
            url("api/lixeiras")
        }.body()
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }
}

@Serializable
data class LocalLixoSer(
    @SerialName("id")
    val id: Long,
    @SerialName("nome")
    val nome: String,
    @SerialName("criador")
    val criador: String,
    @SerialName("latitude")
    val latitude: String,
    @SerialName("longitude")
    val longitude: String,
    @SerialName("estado")
    val estado: String,
    @SerialName("aprovado")
    val aprovado: Boolean,
    @SerialName("foto")
    val foto: String?= null,
)