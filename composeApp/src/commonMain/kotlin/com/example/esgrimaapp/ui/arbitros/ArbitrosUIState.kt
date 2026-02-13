package com.example.esgrimaapp.ui.arbitros

import com.example.esgrimaapp.data.Usuario

data class ArbitrosUIState(
    val mostrarFormulario: Boolean = false,
    val listaArbitrosInscritos: List<Usuario> = emptyList(),
    val usuariosDisponibles: List<Usuario> = emptyList(),
    val nombreCompeticionActiva: String? = null, // null significa que no hay activa
    val hayCompeticionActiva: Boolean = false
)