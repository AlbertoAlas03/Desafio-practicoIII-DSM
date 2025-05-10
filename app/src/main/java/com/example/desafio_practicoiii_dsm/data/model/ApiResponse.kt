package com.example.desafio_practicoiii_dsm.data.model

data class Recurso(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val enlace: String,
    val imagen: String,
    val createdAt: String,
    val updatedAt: String
)

data class ApiResponse(
    val recursos: List<Recurso>
)

data class SingleResourceResponse(
    val recursos: Recurso
)