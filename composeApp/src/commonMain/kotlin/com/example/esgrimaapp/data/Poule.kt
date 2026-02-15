package com.example.esgrimaapp.data

// Representa un grupo individual dentro de la competición
data class Poule(
    val id: String,
    val nombre: String,
    val pista: Int,
    val tiradores: List<Usuario>,
    val arbitroAsignado: Usuario?,
    val asaltosTotales: Int,
    val asaltosCompletados: Int = 0
)