package com.example.splmobile.models.locaisLixo

import co.touchlab.kermit.Logger
import co.touchlab.stately.ensureNeverFrozen
import com.example.splmobile.DatabaseHelper
import com.example.splmobile.database.LocalLixo
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.services.locaisLixo.`LocalLixoService.kt`

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class LocalLixoRepository (
    private val dbHelper: DatabaseHelper,
    private val settings: Settings,
    private val localLixoApi: `LocalLixoService.kt`,
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

    suspend fun createLocalLixo(localLixo: LocalLixo,token :String): String {
        val localLixoResult: RequestMessageResponse = localLixoApi.postLocalLixo(localLixo,token)
       // log.v { "1: ${ localLixo }" }
      //  log.v { "2 result: ${ locaisLixoArray }" }
        log.v { "network result: ${ localLixoResult }" }
        refreshLocaisLixo()

        return localLixoResult.message
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
    /*
    fun updateLocalLixoEstado(localLixo: LocalLixo, s: String): String {
        val localLixoResult: RequestMessageResponse = localLixoApi.patchLocalLixoEstado(localLixo,s)
        // log.v { "1: ${ localLixo }" }
        //  log.v { "2 result: ${ locaisLixoArray }" }
        log.v { "network result: ${ localLixoResult }" }
        dbHelper

        return localLixoResult.message
    }
     */
    //update local storage by deleting old(?) and inserting all
    //todo maybe change to only inserting new lix (if locais lixo updated from backoffice might not appear updated)
   suspend fun refreshLocaisLixo() {
        //log.v { "response ${ localLixo}" }
        val localLixoList = localLixoApi.getLocaisLixo().locaisLixo
        log.v { "locaisLixo  ${ localLixoList }" }

         log.v { "Fetched ${localLixoList.size} locaisLixo from network" }
         settings.putLong(DB_TIMESTAMP_KEY, clock.now().toEpochMilliseconds())
         if (localLixoList.isNotEmpty()) {
             log.v { "IM HERE" }
             dbHelper.deleteAllLocaisLixo()
             dbHelper.insertLocaisLixo(localLixoList)

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
