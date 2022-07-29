package com.example.splmobile.websockets

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
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
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.collections.get

class MessageWebsocket(private val log: Logger, engine: CIO){
    private val client = HttpClient(engine) {
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


     fun connect() {
        log.d { "connect websocket" }
        try {
            runBlocking {
                client.webSocket("ws://10.0.2.2:5001") {}
            }

        } catch (ex: Exception) {
            log.d { "$ex" }
        }

    }

     fun close() {
        log.d { "Fetching garbageSpots from network" }
        try {
           client.close()

        } catch (ex: Exception) {
            log.d { "$ex" }
        }

    }

}


