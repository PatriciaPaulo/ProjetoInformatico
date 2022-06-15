package com.example.splmobile

import co.touchlab.kermit.Logger

import com.example.splmobile.database.LocalLixo
import com.example.splmobile.sqldelight.transactionWithContext
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class DatabaseHelper(
    sqlDriver: SqlDriver,
    private val log: Logger,
    private val backgroundDispatcher: CoroutineDispatcher
) {
    private val dbRef: SPLDatabase = SPLDatabase(sqlDriver)

    fun selectAllItems(): Flow<List<LocalLixo>> =
        dbRef.sPLDatabaseQueries
            .selectAllLocaisLixo()
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)

    suspend fun insertLocaisLixo(locaisLixo: List<JsonElement>) {
        log.d { "Inserting ${locaisLixo.size} locaisLixo into database" }
        dbRef.transactionWithContext(backgroundDispatcher) {

            locaisLixo.forEach { localLixo ->
                val locaisLixoJsonObj =localLixo.jsonObject
                //log.d { " BROOO 2${locaisLixoJsonObj.get("longitude").toString().removePrefix("\"").removeSuffix("\"")} " }


                dbRef.sPLDatabaseQueries.insertLocalLixo(
                    locaisLixoJsonObj.get("id").toString().removePrefix("\"").removeSuffix("\"").toLong(),
                    locaisLixoJsonObj.get("nome").toString().removePrefix("\"").removeSuffix("\""),
                    locaisLixoJsonObj.get("criador").toString().removePrefix("\"").removeSuffix("\""),
                    locaisLixoJsonObj.get("longitude").toString().removePrefix("\"").removeSuffix("\""),
                    locaisLixoJsonObj.get("latitude").toString().removePrefix("\"").removeSuffix("\""),
                    locaisLixoJsonObj.get("estado").toString().removePrefix("\"").removeSuffix("\""),
                    locaisLixoJsonObj.get("aprovado").toString().removePrefix("\"").removeSuffix("\"").toBoolean(),
                    locaisLixoJsonObj.get("foto").toString().removePrefix("\"").removeSuffix("\""))
            }
        }
    }

    fun selectById(id: Long): LocalLixo=
        dbRef.sPLDatabaseQueries
            .selectLocalLixoById(id)
            .executeAsOne()

    suspend fun deleteAllLocaisLixo() {
        log.i { "Database Cleared" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.sPLDatabaseQueries.removeAllLocaisLixo()
        }
    }


}
