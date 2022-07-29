package com.example.splmobile.models

import co.touchlab.kermit.Logger
import com.example.splmobile.dtos.garbageSpots.GarbageSpotsResponse
import com.example.splmobile.services.garbageSpots.GarbageSpotService
import com.example.splmobile.websockets.MessageWebsocket
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch


class MessageViewModel (
    log: Logger
) : ViewModel() {
    private val log = log.withTag("MessageViewModel")
    private val websocket : MessageWebsocket = MessageWebsocket(log)

    fun openConnection() {
        log.v("getting all users in event ")
        websocket.connect()


    }
    fun closeConnection() {
        log.v("getting all users in event ")
        websocket.close()

    }

}
