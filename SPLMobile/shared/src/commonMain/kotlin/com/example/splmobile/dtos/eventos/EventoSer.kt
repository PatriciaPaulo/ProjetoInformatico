package com.example.splmobile.dtos.eventos

import kotlinx.serialization.Serializable


@Serializable
data class EventoSer(
    val id: Long,
    val nome: String,
    val latitude: String,
    val longitude: String,
    val organizador: Long,
    val estado: String,
    val duracao: String,
    val dataInicio: String,
    val descricao: String,
    val acessibilidade: String,
    val restricoes: String,
    val tipoLixo: String,
    val volume: String,
    val foto: String?,
    val observacoes: String?

)