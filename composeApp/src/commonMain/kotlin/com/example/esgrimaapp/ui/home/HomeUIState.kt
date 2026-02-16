package com.example.esgrimaapp.ui.home

import com.example.esgrimaapp.data.Competicion

data class DashboardUIState(
    val hayCompeticion: Boolean = false,
    val nombreComp: String = "",
    val entidad: String = "",
    val fecha: String = "",
    val arma: String = "",
    val lugar: String = "",
    val numTiradores: Int = 0,
    val numArbitros: Int = 0,
    val numPoules: Int = 0,
    val progresoAsaltosGrupo: String = "0/0",
    val progresoEliminatorias: String = "0/0"
)