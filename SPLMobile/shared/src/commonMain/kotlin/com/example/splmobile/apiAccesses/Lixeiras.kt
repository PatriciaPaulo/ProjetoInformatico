package com.example.splmobile.apiAccesses

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class Lixeiras {
    private val client = HttpClient()

    //Pair as Return Type to return 2 variables
    suspend fun loginRequest(username: String, password: String): Pair<Int, String> {
        var responseBody = ""
        var responseCode = 0

        try {
            //POST Request to API
            val getResponse = client.get(API_URL + "lixeiras")

            responseCode = getResponse.status.toString().take(3).toInt()
            println(responseCode.toString().plus("response"))
            print()
            when (responseCode) {
                200 -> {
                    responseBody = JSONObject(getResponse.bodyAsText()).getString("access_token")
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

}