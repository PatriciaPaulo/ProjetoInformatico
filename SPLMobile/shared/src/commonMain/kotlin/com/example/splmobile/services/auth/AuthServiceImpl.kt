package com.example.splmobile.services.auth

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.API_PATH
import com.example.splmobile.dtos.auth.LoginRequest
import com.example.splmobile.dtos.auth.LoginResponse
import com.example.splmobile.dtos.auth.SignInRequest
import com.example.splmobile.dtos.auth.SignInResponse
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

class AuthServiceImpl(
    private val log: Logger,
    engine: HttpClientEngine
):AuthService {
    private val client = HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    log.v { message + "AUTHSERVICE MESSAGE"}
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

    override suspend fun postLogin(loginRequest : LoginRequest): LoginResponse {
        try {
            return client.post {
                url("api/login")
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(loginRequest.email, loginRequest.password))
            }.body()
        }
        catch (e : ClientRequestException){
            return LoginResponse("", e.message)
        }

    }

    override suspend fun postSignIn(signInRequest: SignInRequest): SignInResponse {
        try {
            return client.post {
                url("api/register")
                contentType(ContentType.Application.Json)
                setBody(SignInRequest(signInRequest.email, signInRequest.password, signInRequest.passwordConfirmation))
            }.body()
        } catch (e : ClientRequestException) {
            return SignInResponse(e.message)
        }
    }

    private fun HttpRequestBuilder.url(path: String) {
        url {
            takeFrom(API_PATH)
            encodedPath = path
        }
    }



}