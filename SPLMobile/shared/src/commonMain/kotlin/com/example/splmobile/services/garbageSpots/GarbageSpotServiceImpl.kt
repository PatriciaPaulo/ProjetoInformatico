package com.example.splmobile.services.garbageSpots

import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.dtos.garbageSpots.GarbageSpotSerializable

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

    override suspend fun getGarbageSpots(): GarbageSpotsResponse {
        log.d { "Fetching lixeiras from network" }
        try{
            return client.get {
                contentType(ContentType.Application.Json)
                url("api/lixeiras")
            }.body() as GarbageSpotsResponse
        }catch (ex :HttpRequestTimeoutException){
            return GarbageSpotsResponse(emptyList(),"error","500")
        }
        return GarbageSpotsResponse(emptyList(),"error","400")

    }

    override suspend fun postGarbageSpot(
        garbageSpot: GarbageSpotSerializable,
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
                setBody(GarbageSpotSerializable(garbageSpot.id, garbageSpot.nome, garbageSpot.criador,garbageSpot.latitude,garbageSpot.longitude,garbageSpot.estado,garbageSpot.aprovado,garbageSpot.foto,
                    emptyList()))
                url("api/lixeiras")
            }.body() as RequestMessageResponse
        }
        catch (ex :HttpRequestTimeoutException){
            return RequestMessageResponse("error","500")
        }
        return RequestMessageResponse("error","400")


    }
    override suspend fun patchGarbageSpotStatus(
        garbageSpot: GarbageSpotSerializable,
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
                setBody(GarbageSpotSerializable(garbageSpot.id, garbageSpot.nome, garbageSpot.criador,garbageSpot.latitude,garbageSpot.longitude,status,garbageSpot.aprovado,garbageSpot.foto,
                    emptyList()))
                url("api/lixeiras/"+garbageSpot.id+"/mudarEstadoLixeira")
            }.body() as RequestMessageResponse
        }
        catch (ex :HttpRequestTimeoutException){
            return RequestMessageResponse("error","500")
        }
        return RequestMessageResponse("error","400")
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom("http://10.0.2.2:5000/")
            encodedPath = path
        }
    }


}
