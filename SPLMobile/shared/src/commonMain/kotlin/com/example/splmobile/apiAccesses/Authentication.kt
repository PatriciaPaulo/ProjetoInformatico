package com.example.splmobile.apiAccesses

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.json.JSONObject

const val API_URL = "http://10.0.2.2:5000/api/"

class Authentication {
    private val client = HttpClient()

    //Pair as Return Type to return 2 variables
    suspend fun loginRequest(username: String, password: String): Pair<Int, String> {
        var responseBody = ""
        var responseCode = 0

        try {
            //POST Request to API
            val postResponse = client.post(API_URL + "login") {
                contentType(ContentType.Application.Json)

                val requestBody = JSONObject()
                requestBody.put("username", username)
                requestBody.put("password", password)

                setBody(requestBody.toString())
            }

            responseCode = postResponse.status.toString().take(3).toInt()

            when (responseCode) {
                200 -> {
                    responseBody = JSONObject(postResponse.bodyAsText()).getString("access_token")
                }
                401 -> {
                    //TODO change text to android resources
                    responseBody = "TXT FOR 401"
                }
                404 -> {
                    //TODO change text to android resources
                    responseBody = "TXT FOR 404"
                }
                else -> {
                    //TODO change text to android resources
                    responseBody = "TXT FOR ELSE"
                }
            }
        } catch (exception: Exception) {
            //TODO Write API Exception
        }

        return Pair(responseCode, responseBody)
    }


    suspend fun registerRequest(username: String,name: String,email:String, password: String, passwordConfirmation: String): Pair<Int, String> {
        var responseBody = ""
        var responseCode = 0

        try {
            //POST Request to API
            val postResponse = client.post(API_URL + "register") {
                contentType(ContentType.Application.Json)

                val requestBody = JSONObject()
                requestBody.put("username", username)
                requestBody.put("name", name)
                requestBody.put("email", email)
                requestBody.put("password", password)
                requestBody.put("passwordConfirmation", passwordConfirmation)

                setBody(requestBody.toString())
            }

            responseCode = postResponse.status.toString().take(3).toInt()

            when (responseCode) {
                200 -> {
                    responseBody = JSONObject(postResponse.bodyAsText()).getString("message")
                }
                400 -> {
                    //TODO change text to android resources
                    responseBody = "TXT FOR 400"
                }
                404 -> {
                    //TODO change text to android resources
                    responseBody = "TXT FOR 404"
                }
                409 -> {
                    //TODO change text to android resources
                    responseBody = "Já existe um utilizador com este username!"
                }
                else -> {
                    //TODO change text to android resources
                    responseBody = "TXT FOR ELSE"
                }
            }
        } catch (exception: Exception) {
            //TODO Write API Exception
        }

        return Pair(responseCode, responseBody)
    }
}