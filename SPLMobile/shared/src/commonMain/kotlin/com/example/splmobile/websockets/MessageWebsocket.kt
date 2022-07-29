package com.example.splmobile.websockets

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.RequestMessageResponse
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject


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


     fun connect(token:String) {
        log.d { "connect websocket" }

         try {
            GlobalScope.launch(Dispatchers.Unconfined) {
                client.webSocket("ws://10.0.2.2:5001", request = {
                    header("token", "Bearer "+token)
                }) {
                    try {
                        while (true) {
                            val othersMessage = incoming.receive() as? Frame.Text ?: continue
                            println( "bit - "+ othersMessage.readText())
                            //TODO PARS
                            val jsonObject = Json.parseToJsonElement(othersMessage.readText())
                            println("js 1- "+ jsonObject)
                            val jsonObject2 = Json.parseToJsonElement(jsonObject.jsonObject["id"].toString())
                            println("js 2- "+ jsonObject2)
                            //val aaa = Json.parseToJsonElement(jsonObject2.jsonObject["id\\"].toString())
                            send(jsonObject2.toString())

                        }
                    }catch (ex: Exception) {
                        client.close()
                        log.d { "$ex" }
                    }


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

