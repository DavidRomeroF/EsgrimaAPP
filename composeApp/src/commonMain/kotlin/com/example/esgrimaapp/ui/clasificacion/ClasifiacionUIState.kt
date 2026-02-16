package com.example.esgrimaapp.ui.clasificacion

import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.Usuario

data class ClasifiacionUIState (
    val compId: String = "",
    val tamanoSeleccionado: Int = 0,
    val asaltosExistentes: List<Asalto> = emptyList(),
    val rankingActual: List<EstadisticasTirador> = emptyList(),
    val faseGruposTerminada: Boolean = false,
    val arbitrosDisponibles: List<Usuario> = emptyList()
)