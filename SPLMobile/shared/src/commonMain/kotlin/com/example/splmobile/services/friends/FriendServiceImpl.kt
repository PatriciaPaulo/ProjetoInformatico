package com.example.splmobile.services.friends

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.events.EventsResponse
import com.example.splmobile.dtos.users.FriendsResponse
import com.example.splmobile.dtos.users.UsersStatsResponse
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

class FriendServiceImpl (private val log: Logger, engine: HttpClientEngine) : FriendService {
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

    override suspend fun getAllFriends(
        token: String
    ): FriendsResponse {
        log.d { "Fetching events from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }

                url("api/friends/me")
            }.body() as FriendsResponse
        }catch (ex :Exception){
            return FriendsResponse(emptyList(),"$ex")
        }
    }

    override suspend fun getFriendByID(
        id: Long, token: String
    ): RequestMessageResponse {
        log.d { "Fetching events from network" }
        try{
            return client.get {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                url("api/users/"+id+"/friend")
            }.body() as RequestMessageResponse
        }catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }
    }



    override suspend fun postFriendRequest(
        userID: Long, token: String
    ): RequestMessageResponse {
        log.d { "post new Garbage Spot" }
        try{
            return client.post {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(userID)

                url("api/friends")
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }
    }

    override suspend fun removeFriend(
        friendshipID: Long, token: String
    ): RequestMessageResponse {
        log.d { "remove friend" }
        try{
            return client.delete() {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                url("api/friends/"+friendshipID)
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }

}