package com.example.splmobile.services.websockets

import co.touchlab.kermit.Logger
import com.example.splmobile.objects.events.EventDTO
import com.example.splmobile.objects.myInfo.UserSerializable
import com.example.splmobile.models.MessageViewModel
import com.example.splmobile.HttpRequestUrls
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject


class MessageWebsocket(
    private val log: Logger,
    private val messageViewModel: MessageViewModel,
) {
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
                    log.v { message + "API IMP MESSAGE" }
                }
            }

            level = LogLevel.INFO
        }

    }


    suspend fun websocket(token: String) {

        client.webSocket(HttpRequestUrls.websocket_emulator.url, request = {
            header("token", "Bearer " + token)
        }) {
            try {
                while (true) {
                    val othersMessage = incoming.receive() as? Frame.Text ?: continue
                    println("bit - " + othersMessage.readText())
                    //TODO PARS
                    val jsonObject = Json.parseToJsonElement(othersMessage.readText())
                    println("js 1- " + jsonObject)
                    val message =
                        Json.parseToJsonElement(jsonObject.jsonObject["message"].toString())


                    if (Json.encodeToString(message).replace("\"", "") == "friendRequest") {
                        println("friendRequest received")
                        val user = Json.parseToJsonElement(jsonObject.jsonObject["user"].toString())
                        messageViewModel.notificationFriendRequest(
                            Json.decodeFromJsonElement<UserSerializable>(
                                user
                            )
                        )
                    } else {
                        if (Json.encodeToString(message).replace("\"", "") == "eventStatus") {
                            println("event status changed received")
                            val event =
                                Json.parseToJsonElement(jsonObject.jsonObject["event"].toString())
                            messageViewModel.notificationEventStatus(
                                Json.decodeFromJsonElement<EventDTO>(
                                    event
                                )
                            )
                        } else {
                            val type =
                                Json.parseToJsonElement(message.jsonObject["type"].toString())
                            println("type of message - $type")
                            if (Json.encodeToString(type).replace("\"", "") == "Event") {
                                println("event chat message received")
                                val eventID =
                                    Json.parseToJsonElement(jsonObject.jsonObject["eventID"].toString())
                                messageViewModel.messageNotificationEvent(
                                    eventID.toString().toLong()
                                )
                            }
                            if (Json.encodeToString(type).replace("\"", "") == "Individual") {
                                println("individual chat message received")
                                val userID =
                                    Json.parseToJsonElement(message.jsonObject["senderID"].toString())
                                messageViewModel.messageNotification(userID.toString().toLong())
                            }

                        }
                        val id = Json.parseToJsonElement(message.jsonObject["id"].toString())
                        println("Sending reply ${id}")
                        send(id.toString())
                    }


                }
            } catch (ex: Exception) {

                log.d { "$ex" }
            }
        }
    }

    fun connect(token: String) {
        log.d { "connect websocket" }
        try {
            GlobalScope.launch(Dispatchers.Unconfined) {
                websocket(token)
            }
        } catch (ex: Exception) {
            log.d { "$ex" }
        } catch (ex: ConnectTimeoutException) {
            MessageViewModel.NotificationUIState.Offline
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

