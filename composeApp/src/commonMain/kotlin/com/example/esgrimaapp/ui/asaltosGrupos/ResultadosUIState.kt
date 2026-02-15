package com.example.esgrimaapp.ui.asaltosGrupos

import com.example.esgrimaapp.data.Asalto

data class ResultadosUIState(
    val faseGruposActiva: Boolean = true, // Para el selector de pestañas
    val hayGrupos: Boolean = false,
    val asaltosPorGrupo: Map<String, List<Asalto>> = emptyMap(),
    val nombreCompeticion: String? = null,
    val asaltoParaEditar: Asalto? = null // Para el diálogo
)