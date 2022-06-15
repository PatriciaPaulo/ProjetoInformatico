package com.example.splmobile.OLDSTUFF.apiAccesses.shared.cache

/*
import com.example.splmobile.core.entity.Lixeira

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllLixeiras()
            dbQuery.removeAllUtilizadores()
        }
    }

    internal fun getAllLixeiras(): List<Lixeira> {
        return dbQuery.selectAllLixeirasFromUtilizador(::mapLixeira).executeAsList()
    }

    private fun mapLixeira(
        id: Long,
        nome: String,
        criador: Long,
        latitude: String,
        longitude: String,
        estado: String,
        aprovado: Boolean,
        foto: String?
    ): Lixeira {
        return Lixeira(
            id = id.toInt(),
            nome = nome,
            criador = criador.toInt(),
            latitude = latitude,
            longitude = longitude,
            estado = estado,
            aprovado = aprovado,
            foto = foto
        )
    }

    internal fun createLixeiras(lixeixas: List<Lixeira>) {
        dbQuery.transaction {
            lixeixas.forEach { lixeixa ->
                val lix = dbQuery.selectLixeiraById(lixeixa.id.toLong()).executeAsOneOrNull()
                if (lix != null) {
                    insertLixeira(lix)
                }

            }
        }
    }

    private fun insertLixeira(lixeira: com.example.splmobile.database.Lixeira) {
        dbQuery.insertLixeira(
            id = lixeira.id,
            nome = lixeira.nome,
            criador = lixeira.criador,
            latitude = lixeira.latitude,
            longitude = lixeira.longitude,
            aprovado = lixeira.aprovado,
            estado = lixeira.estado,
            foto = lixeira.foto

        )
    }



}*/