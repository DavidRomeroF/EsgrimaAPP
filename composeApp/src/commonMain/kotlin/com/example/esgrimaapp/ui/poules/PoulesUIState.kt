package com.example.esgrimaapp.ui.poules

import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.Poule
import com.example.esgrimaapp.data.Usuario

data class PoulesUIState(
    // --- Control de Flujo ---
    val hayCompeticionActiva: Boolean = false,
    val nombreCompeticion: String? = null,

    // --- Datos de Origen (Inscritos) ---
    val tiradoresInscritos: List<Usuario> = emptyList(),
    val arbitrosInscritos: List<Usuario> = emptyList(),

    // --- Configuración de Generación (Inputs) ---
    val cantidadGrupos: String = "2", // Usamos String para los TextField
    val cantidadPistas: String = "1",

    // --- Resultado ---
    val gruposGenerados: List<Poule> = emptyList(),

    val todosLosAsaltos: List<Asalto> = emptyList(),
    // --- UI Helpers ---
    val cargando: Boolean = false,
    val errorMensaje: String? = null
) {
    // Propiedades calculadas para facilitar la lógica en la UI
    val puedeGenerar: Boolean
        get() = hayCompeticionActiva &&
                tiradoresInscritos.size >= 6 &&
                cantidadGrupos.toIntOrNull() != null &&
                cantidadGrupos.toInt() >= 2

    val infoReparto: String
        get() {
            val nGrupos = cantidadGrupos.toIntOrNull() ?: return ""
            if (nGrupos <= 0) return ""
            val base = tiradoresInscritos.size / nGrupos
            val resto = tiradoresInscritos.size % nGrupos
            return if (resto == 0) "$nGrupos grupos de $base"
            else "$resto grupos de ${base + 1} y ${nGrupos - resto} de $base"
        }
}