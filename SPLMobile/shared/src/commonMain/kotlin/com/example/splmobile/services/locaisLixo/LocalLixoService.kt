package com.example.splmobile.services.locaisLixo

import com.example.splmobile.database.LocalLixo
import com.example.splmobile.dtos.RequestMessageResponse
import com.example.splmobile.dtos.locaisLixo.LocaisLixoResponse
import com.example.splmobile.dtos.locaisLixo.LocalLixoSer


interface LocalLixoService {
      suspend fun getLocaisLixo(): LocaisLixoResponse
      suspend fun postLocalLixo(localLixo: LocalLixoSer,token: String): RequestMessageResponse
      suspend fun patchLocalLixoEstado(localLixo: LocalLixoSer, estado: String, token: String): RequestMessageResponse
}