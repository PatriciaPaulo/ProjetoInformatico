package com.example.splmobile.OLDSTUFF.apiAccesses.shared
/*
import com.example.splmobile.OLDSTUFF.apiAccesses.shared.cache.Database
import com.example.splmobile.OLDSTUFF.apiAccesses.shared.cache.DatabaseDriverFactory
import com.example.splmobile.core.entity.Lixeira
import com.example.splmobile.network.SPLAPI
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class AppStore(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = Database(databaseDriverFactory)
    private val api = SPLAPI()

    @Throws(Exception::class) suspend fun getLixeiras(forceReload: Boolean): List<Lixeira> {
        val cachedLixeiras = database.getAllLixeiras()

        return if (cachedLixeiras.isNotEmpty() && !forceReload) {
            cachedLixeiras
        } else {

            getLixeirasFromResponse()
        }
    }







    private suspend fun getLixeirasFromResponse(): List<Lixeira> {
        var rsCode = 0
        var rsBody = ""

        var lixeiras: MutableList<Lixeira> = arrayListOf()


        val (responseCode, responseBody) =  api.getAllLixeiras()
        rsCode = responseCode
        rsBody = responseBody

        when (rsCode) {
            200 -> {
                database.clearDatabase()
                val array = Json.parseToJsonElement(rsBody).jsonArray
                for (i in 0 until array.size) {
                    val item = array.get(i)

                    lixeiras.add(
                        Lixeira(
                            item.jsonObject.getValue("id").toString().toInt(),
                            item.jsonObject.getValue("nome").toString(),
                            item.jsonObject.getValue("criador").toString().toInt(),
                            item.jsonObject.getValue("latitude").toString(),
                            item.jsonObject.getValue("longitude").toString(),
                            item.jsonObject.getValue("aprovado").toString().toBoolean(),
                            item.jsonObject.getValue("estado").toString(),
                            item.jsonObject.getValue("foto").toString()
                        )
                    )


                }
                println(lixeiras.plus("lixeiras a criar"))
                database.createLixeiras(lixeiras)
            }
            else -> {
                println("SPL SDK - ERRRROORRR")
            }
        }
        return lixeiras;
    }

}
*/