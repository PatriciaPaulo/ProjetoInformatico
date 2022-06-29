package com.example.splmobile

import co.touchlab.kermit.Logger
import com.example.splmobile.database.LocalLixo

import com.example.splmobile.dtos.locaisLixo.LocalLixoSer
import com.example.splmobile.sqldelight.transactionWithContext
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class DatabaseHelper(
    val sqlDriver: SqlDriver,
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

    suspend fun insertLocaisLixo(locaisLixo: List<LocalLixoSer>) {

        log.d { "Inserting ${locaisLixo.size} locaisLixo into database" }
        dbRef.transactionWithContext(backgroundDispatcher) {
            locaisLixo.forEach { localLixo ->
                log.d { " lat  ${localLixo.latitude}"}
                log.d { " long  ${localLixo.longitude}"}
                dbRef.sPLDatabaseQueries.insertLocalLixo(localLixo.id,localLixo.nome,localLixo.criador,localLixo.latitude,localLixo.longitude,localLixo.estado,localLixo.aprovado,localLixo.foto,localLixo.eventos.toString())
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
