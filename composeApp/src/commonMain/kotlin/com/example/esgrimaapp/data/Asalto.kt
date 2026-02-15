package com.example.esgrimaapp.data

enum class EstadoAsalto { PROGRAMADO, EN_CURSO, FINALIZADO, INCOMPARECENCIA_A, INCOMPARECENCIA_B, INCOMPARECENCIA_AMBOS }

data class Asalto(
    val id: String,
    val grupoId: String,
    val tiradorA: Usuario,
    val tiradorB: Usuario,
    val tocadosA: Int = 0,
    val tocadosB: Int = 0,
    val arbitro: Usuario?,
    val pista: Int,
    val estado: EstadoAsalto = EstadoAsalto.PROGRAMADO,
    val horaEstimada: String
)