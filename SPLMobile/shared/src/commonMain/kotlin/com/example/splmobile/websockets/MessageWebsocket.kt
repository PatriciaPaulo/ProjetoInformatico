package com.example.splmobile.websockets

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageTypes.GarbageTypesResponse
import com.example.splmobile.services.garbageSpots.GarbageSpotService
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.collections.get


class MessageWebsocket(private val log: Logger){
    private val client = HttpClient(CIO) {
        expectSuccess = true
        engine {
            // this: CIOEngineConfig
            maxConnectionsCount = 1000
            endpoint {
                // this: EndpointConfig
                maxConnectionsPerRoute = 100
                pipelineMaxSize = 20
                keepAliveTime = 5000
                connectTimeout = 5000
                connectAttempts = 5
            }
        }
        install(WebSockets)
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    log.v { message + "API IMP MESSAGE"}
                }
            }

            level = LogLevel.INFO
        }

    }


    suspend fun connect() {
        log.d { "connect websocket" }
        try {

            client.webSocket("ws://10.0.2.2:5001") {
                while(true) {
                    val othersMessage = incoming.receive() as? Frame.Text ?: continue
                    println(othersMessage.readText())

                }
            }

        } catch (ex: Exception) {
            log.d { "$ex" }
        }

    }

    fun close() {
        log.d { "CLOSE websocket" }
        try {
            client.close()

        } catch (ex: Exception) {
            log.d { "$ex" }
        }

    }

}

