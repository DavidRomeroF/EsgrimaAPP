package com.example.esgrimaapp.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.Competicion
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.data.Poule
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.FencingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(DashboardUIState())
    val uiState = _uiState.asStateFlow()

    init {
        observarDatos()
    }

    private fun observarDatos() {
        screenModelScope.launch {
            combine(
                FencingRepository.idCompeticionActiva,    // 0
                FencingRepository.competiciones,           // 1
                FencingRepository.inscripciones,           // 2
                FencingRepository.arbitrosInscritos,        // 3
                FencingRepository.poulesCompeticion,       // 4
                FencingRepository.asaltosCompeticion,      // 5
                FencingRepository.asaltosEliminacion       // 6
            ) { flows ->
                val idActivo = flows[0] as? String
                val todasLasComps = flows[1] as List<Competicion>

                val compReal = todasLasComps.find { it.id == idActivo }

                if (compReal == null) {
                    DashboardUIState(hayCompeticion = false)
                } else {
                    // Casteos seguros para evitar errores en tiempo de ejecución
                    val inscritos = flows[2] as Map<String, List<Usuario>>
                    val arbitros = flows[3] as Map<String, List<Usuario>>
                    val poules = flows[4] as Map<String, List<Poule>>
                    val asaltosG = flows[5] as Map<String, List<Asalto>>
                    val asaltosE = flows[6] as Map<String, List<Asalto>>

                    DashboardUIState(
                        hayCompeticion = true,
                        nombreComp = compReal.nombre,
                        entidad = compReal.entidad,
                        fecha = compReal.fecha,
                        arma = compReal.arma,
                        lugar = compReal.lugar,
                        numTiradores = inscritos[idActivo]?.size ?: 0,
                        numArbitros = arbitros[idActivo]?.size ?: 0,
                        numPoules = poules[idActivo]?.size ?: 0,
                        progresoAsaltosGrupo = calcularProgreso(asaltosG, idActivo),
                        progresoEliminatorias = calcularProgreso(asaltosE, idActivo)
                    )
                }
            }.collect { nuevoEstado ->
                _uiState.update { nuevoEstado }
            }
        }
    }

    // Corregido: idActivo ahora es String para que coincida con el Mapa
    private fun calcularProgreso(mapaAsaltos: Map<String, List<Asalto>>, idActivo: String?): String {
        val asaltos = mapaAsaltos[idActivo] ?: return "0/0"

        val totales = asaltos.size
        val finalizados = asaltos.count { it.estado == EstadoAsalto.FINALIZADO }

        return "$finalizados/$totales"
    }
}