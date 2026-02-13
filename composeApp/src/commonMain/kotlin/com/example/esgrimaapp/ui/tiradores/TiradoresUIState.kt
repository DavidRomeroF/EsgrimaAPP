package com.example.esgrimaapp.ui.tiradores

import com.example.esgrimaapp.data.Usuario

data class TiradoresUIState(
    val mostrarFormulario: Boolean = false,
    val listaTiradoresInscritos: List<Usuario> = emptyList(),
    val usuariosDisponibles: List<Usuario> = emptyList(),
    val nombreCompeticionActiva: String? = null, // null significa que no hay activa
    val hayCompeticionActiva: Boolean = false
)