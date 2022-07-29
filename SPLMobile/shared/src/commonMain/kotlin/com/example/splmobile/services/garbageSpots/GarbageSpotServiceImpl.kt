package com.example.splmobile.services.garbageSpots

import GarbageSpotResponse
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotDTO
import com.example.splmobile.dtos.garbageTypes.GarbageTypesResponse

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger


class GarbageSpotServiceImpl(private val log: KermitLogger, engine: HttpClientEngine) : GarbageSpotService{
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


    }

    init {
        ensureNeverFrozen()
    }

    override suspend fun getGarbageSpots(token: String): GarbageSpotsResponse {
        log.d { "Fetching garbageSpots from network" }
        try{
            return client.get {
                if(token.isNotEmpty()){
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                contentType(ContentType.Application.Json)
                url("api/garbageSpots")
            }.body() as GarbageSpotsResponse
        }catch (ex :Exception){
            return GarbageSpotsResponse(emptyList(),"$ex")
        }


    }

    override suspend fun postGarbageSpot(
        garbageSpot: GarbageSpotDTO,
        token: String
    ): RequestMessageResponse {
        log.d { "post new Garbage Spot" }
        try{
            return client.post {
                if(token.isNotEmpty()){
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(GarbageSpotDTO(garbageSpot.id, garbageSpot.name, garbageSpot.creator,garbageSpot.latitude,garbageSpot.longitude,garbageSpot.status,garbageSpot.approved,
                    emptyList()))
                url("api/garbageSpots")
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }


    }
    override suspend fun patchGarbageSpotStatus(
        garbageSpot: GarbageSpotDTO,
        status: String,
        token: String,
    ): RequestMessageResponse {
        log.d { "update Garbage Spot" }
        try{
            return client.patch {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(GarbageSpotDTO(garbageSpot.id, garbageSpot.name, garbageSpot.creator,garbageSpot.latitude,garbageSpot.longitude,status,garbageSpot.approved,
                    emptyList()))
                url("api/garbageSpots/"+garbageSpot.id+"/updateGarbageSpotStatus")
            }.body() as RequestMessageResponse
        }
        catch (ex :Exception){
            return RequestMessageResponse("$ex")
        }

    }

    override suspend fun getGarbageTypes(token: String): GarbageTypesResponse {
        try{
            return client.get {
                contentType(ContentType.Application.Json)
                url("api/garbageTypes")
            }.body() as GarbageTypesResponse
        }catch (ex :Exception){
            return GarbageTypesResponse(emptyList(),"$ex")
        }

    }

    override suspend fun getGarbageSpotById(
        gsId: Long, token: String
    ): GarbageSpotResponse {
        try{
            return client.get {
                contentType(ContentType.Application.Json)
                url("api/garbageSpots/"+gsId)
            }.body() as GarbageSpotResponse
        }catch (ex :Exception){
            return GarbageSpotResponse(GarbageSpotDTO(0,"",0,"","","",false, emptyList()),"$ex")
        }

    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }


}
