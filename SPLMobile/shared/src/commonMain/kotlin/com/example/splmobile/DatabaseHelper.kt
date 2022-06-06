package com.example.splmobile

import co.touchlab.kermit.Logger
import com.example.splmobile.database.Lixeira
import com.example.splmobile.AppDatabase
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
    private val dbRef: AppDatabase = AppDatabase(sqlDriver)

    fun selectAllItems(): Flow<List<Lixeira>> =
        dbRef.appDatabaseQueries
            .selectAllLixeiras()
            .asFlow()
            .mapToList()
            .flowOn(backgroundDispatcher)

    suspend fun insertLixeiras(lixeiras: List<JsonElement>) {
        log.d { "Inserting ${lixeiras.size} lixeiras into database" }
        dbRef.transactionWithContext(backgroundDispatcher) {

            lixeiras.forEach { lixeira ->
                val lixeiraJsonObj =lixeira.jsonObject
                //log.d { " BROOO 2${lixeiraJsonObj.get("longitude").toString().removePrefix("\"").removeSuffix("\"")} " }


                dbRef.appDatabaseQueries.insertLixeira(
                    lixeiraJsonObj.get("id").toString().removePrefix("\"").removeSuffix("\"").toLong(),
                    lixeiraJsonObj.get("nome").toString().removePrefix("\"").removeSuffix("\""),
                    lixeiraJsonObj.get("criador").toString().removePrefix("\"").removeSuffix("\"").toLong(),
                    lixeiraJsonObj.get("longitude").toString().removePrefix("\"").removeSuffix("\""),
                    lixeiraJsonObj.get("latitude").toString().removePrefix("\"").removeSuffix("\""),
                    lixeiraJsonObj.get("estado").toString().removePrefix("\"").removeSuffix("\""),
                    lixeiraJsonObj.get("aprovado").toString().removePrefix("\"").removeSuffix("\"").toBoolean(),
                    lixeiraJsonObj.get("foto").toString().removePrefix("\"").removeSuffix("\""))
            }
        }
    }

    fun selectById(id: Long): Lixeira=
        dbRef.appDatabaseQueries
            .selectLixeiraById(id)
            .executeAsOne()

    suspend fun deleteAllLixeiras() {
        log.i { "Database Cleared" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            dbRef.appDatabaseQueries.removeAllLixeiras()
        }
    }


}
