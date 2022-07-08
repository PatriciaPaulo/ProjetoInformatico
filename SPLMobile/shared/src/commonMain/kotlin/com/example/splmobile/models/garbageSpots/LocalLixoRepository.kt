package com.example.splmobile.models.garbageSpots

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen

import com.example.splmobile.services.garbageSpots.GarbageSpotService


import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock

class LocalLixoRepository (
   // private val dbHelper: DatabaseHelper,
    private val settings: Settings,
    private val garbageSpotService: GarbageSpotService,
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


    suspend fun deleteLocaisLixo() {
       // dbHelper.deleteAllLocaisLixo()

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
