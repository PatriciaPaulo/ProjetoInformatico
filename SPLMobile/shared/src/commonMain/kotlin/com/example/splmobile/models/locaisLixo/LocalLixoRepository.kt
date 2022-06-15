package com.example.splmobile.models.locaisLixo

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.DatabaseHelper
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.services.locaisLixo.LocalLixoApi

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class LocalLixoRepository (
    private val dbHelper: DatabaseHelper,
    private val settings: Settings,
    private val localLixoApi: LocalLixoApi,
    log: Logger,
    private val clock: Clock
) {

    private val log = log.withTag("LocalLixoViewModel")

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"
    }

    init {
        ensureNeverFrozen()
    }

    suspend fun createLocalLixo(localLixo: LocalLixo): String {
        val localLixoResult = localLixoApi.postLocalLixo(localLixo)
       // log.v { "1: ${ localLixo }" }
      //  log.v { "2 result: ${ locaisLixoArray }" }
        log.v { "3 network result: ${ localLixoResult }" }
        refreshLocaisLixo()
        return localLixoResult.toString()
    }

    fun getLocaisLixo(): Flow<List<LocalLixo>> = dbHelper.selectAllItems()

    fun getLocalLixoById(id: Long): LocalLixo {
       return dbHelper.selectById(id)
    }
    suspend fun refreshLocaisLixoIfStale() {
        if (isLocalLixoListStale()) {
            refreshLocaisLixo()
        }
    }

    suspend fun refreshLocaisLixo() {
        val localLixoResult = localLixoApi.getLocaisLixoJson()
        var locaisLixo = Json.parseToJsonElement(localLixoResult!!.toString()).jsonObject.get("data")
        var locaisLixoArray = Json.parseToJsonElement(locaisLixo.toString()).jsonArray

        log.v { "locaisLixo network result: ${ locaisLixoArray.toList() }" }
        log.v { "Fetched ${locaisLixoArray.size} locaisLixo from network" }
        settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        if (locaisLixoArray.isNotEmpty()) {
            dbHelper.insertLocaisLixo(locaisLixoArray.toList())

        }
    }

    suspend fun deleteLocaisLixo() {
        dbHelper.deleteAllLocaisLixo()

    }



    private fun isLocalLixoListStale(): Boolean {
        val lastDownloadTimeMS = settings.getLong(DB_TIMESTAMP_KEY, 0)
        val oneHourMS = 60 * 60 * 1000
        val stale = lastDownloadTimeMS + oneHourMS < clock.now().toEpochMilliseconds()
        if (!stale) {
            log.i { "locaisLixo not fetched from network. Recently updated" }
        }
        return stale
    }
}
