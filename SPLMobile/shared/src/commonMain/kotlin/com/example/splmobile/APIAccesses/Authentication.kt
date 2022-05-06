package com.example.splmobile.APIAccesses

import com.example.splmobile.Platform
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.*
import org.json.JSONObject

const val API_URL = "http://10.0.2.2:5000/api/"

class Authentication {
    private val client = HttpClient()

    suspend fun loginRequest(username: String, password: String): String {
        val response: HttpResponse = client.post(API_URL+"login") {
            contentType(ContentType.Application.Json)

            val requestBody = JSONObject()
            requestBody.put("username", "${}");
            requestBody.put("password", "123");

            setBody(requestBody.toString())
        }
        return response.bodyAsText()
    }
}