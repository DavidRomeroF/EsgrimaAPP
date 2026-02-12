package com.example.esgrimaapp.data

enum class Arma { SABLE, ESPADA, FLOR }
data class Competicion(
    val id: String,
    val nombre: String,
    val entidad: String,
    val fecha: String,
    val lugar: String,
    val arma: String
)