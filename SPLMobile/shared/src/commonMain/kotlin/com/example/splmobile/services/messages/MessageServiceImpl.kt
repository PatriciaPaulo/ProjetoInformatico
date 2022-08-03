package com.example.splmobile.services.messages

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageTypes.GarbageTypesResponse
import com.example.splmobile.dtos.messages.IndividualMessageRequest
import com.example.splmobile.dtos.messages.MessageDTO
import com.example.splmobile.dtos.messages.MessageResponse
import com.example.splmobile.dtos.messages.MessagesResponse
import com.example.splmobile.services.garbageSpots.GarbageSpotService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
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





    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }

    override suspend fun getMessages(
        userID: Long, token: String
    ): MessagesResponse {
        log.d { "Fetching messages from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url("api/friends/"+userID+"/messages")
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

    override suspend fun getLastMessage(
        userID: Long, token: String
    ): MessageResponse {
        log.d { "Fetching last message from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url("api/friends/"+userID+"/lastMessage")
            }.body() as MessageResponse
        }catch (ex :Exception){
            return MessageResponse(MessageDTO(0,"",0,0,null,""),"$ex")
        }
    }


}
