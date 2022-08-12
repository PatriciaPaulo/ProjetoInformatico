package com.example.splmobile.services.messages

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.messages.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


class MessageServiceImpl(private val log: Logger, engine: HttpClientEngine) :
    MessageService {
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
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


    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getMessages(
        friendshipID: Long, token: String
    ): MessagesResponse {
        log.d { "Fetching messages from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url("api/friends/"+friendshipID+"/messages")
            }.body() as MessagesResponse
        }catch (ex :Exception){
            return MessagesResponse(emptyList(),"$ex")
        }

    }
    override suspend fun getEventMessages(
        eventID: Long, token: String
    ): MessagesResponse {
        log.d { "Fetching messages in event from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url("api/events/"+eventID+"/messages")
            }.body() as MessagesResponse
        }catch (ex :Exception){
            return MessagesResponse(emptyList(),"$ex")
        }

    }

    override suspend fun postMessage(
        message: IndividualMessageRequest,
        token: String
    ): RequestMessageResponse {
        log.d { "post new message" }
        try{
            return client.post {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(message)
                url("api/messages")
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }
    }

    override suspend fun postEventMessage(
        message: EventMessageRequest,
        token: String
    ): RequestMessageResponse {
        log.d { "post new message" }
        try{
            return client.post {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(message)
                url("api/messagesEvent")
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }
    }

    override suspend fun getLastMessage(
        token: String
    ): MessagesResponse {
        log.d { "Fetching last message with friend from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }

                url("api/friends/lastMessage")
            }.body() as MessagesResponse
        }catch (ex :Exception){
            return MessagesResponse(emptyList(),"$ex")
        }
    }

    override suspend fun getLastEventMessage(
       token: String
    ): MessagesResponse {
        log.d { "Fetching last message in event chat from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url("api/events/lastMessage")
            }.body() as MessagesResponse
        }catch (ex :Exception){
            return MessagesResponse(emptyList(),"$ex")
        }
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }
}
