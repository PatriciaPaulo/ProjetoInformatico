package com.example.splmobile.models.lixeiras

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.DatabaseHelper
import com.example.splmobile.database.Lixeira
import com.example.splmobile.ktor.lixeiras.LixeiraApi
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class LixeiraRepository (
    private val dbHelper: DatabaseHelper,
    private val settings: Settings,
    private val lixeiraApi: LixeiraApi,
    log: Logger,
    private val clock: Clock
) {

    private val log = log.withTag("LixeiraViewModel")

    companion object {
        internal const val DB_TIMESTAMP_KEY = "DbTimestampKey"
    }

    init {
        ensureNeverFrozen()
    }

    fun getLixeiras(): Flow<List<Lixeira>> = dbHelper.selectAllItems()

    suspend fun refreshLixeirasIfStale() {
        if (isLixeiraListStale()) {
            refreshLixeiras()
        }
    }

    suspend fun refreshLixeiras() {


        val lixeiraResult = lixeiraApi.getJsonFromApi()
        var lixeiras = Json.parseToJsonElement(lixeiraResult!!.toString()).jsonObject.get("data")
        var lixeirasArray = Json.parseToJsonElement(lixeiras.toString()).jsonArray

        log.v { "lixeira network result: ${ lixeirasArray.toList() }" }
        log.v { "Fetched ${lixeirasArray.size} lixeiras from network" }
        settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
        if (lixeirasArray.isNotEmpty()) {
            dbHelper.deleteAllLixeiras()
            dbHelper.insertLixeiras(lixeirasArray.toList())

        }
    }

    suspend fun deleteLixeiras() {
        dbHelper.deleteAllLixeiras()

    }



    private fun isLixeiraListStale(): Boolean {
        val lastDownloadTimeMS = settings.getLong(DB_TIMESTAMP_KEY, 0)
        val oneHourMS = 60 * 60 * 1000
        val stale = lastDownloadTimeMS + oneHourMS < clock.now().toEpochMilliseconds()
        if (!stale) {
            log.i { "Lixeiras not fetched from network. Recently updated" }
        }
        return stale
    }
}
