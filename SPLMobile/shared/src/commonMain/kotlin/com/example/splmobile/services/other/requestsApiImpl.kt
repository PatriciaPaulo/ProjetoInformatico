package com.example.splmobile.services.other

import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.HttpRequestUrls
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class requestsApiImpl(private val log: co.touchlab.kermit.Logger, engine: HttpClientEngine) : requestsAPI {
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json()

        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    log.v { message + "API IMP MESSAGE" }
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
    }

    init {
        ensureNeverFrozen()
    }

     override suspend fun getJsonFromApi(place: String): String {

        log.d { "post GEOCODE" }
        return client.get {
            url("$place?json=1")
        }.body()

    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom(HttpRequestUrls.api_geocode.url)
            encodedPath = path
        }
    }
}