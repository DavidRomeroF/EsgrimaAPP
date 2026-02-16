package com.example.esgrimaapp.ui.clasificacion

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.FencingRepository
import com.example.esgrimaapp.utilits.RankingLogic
import com.example.esgrimaapp.utilits.SeedingLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClasificacionViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(ClasifiacionUIState())
    val uiState = _uiState.asStateFlow()

    init {
        observarDatos()
    }

    private fun observarDatos() {
        screenModelScope.launch {
            // Al ser 6 flujos, usamos la sintaxis de array para evitar el límite de 5
            combine(
                FencingRepository.idCompeticionActiva,      // 1
                FencingRepository.tablonConfig,             // 2
                FencingRepository.asaltosEliminacion,       // 3
                FencingRepository.asaltosCompeticion,       // 4
                FencingRepository.inscripciones,            // 5
                FencingRepository.arbitrosInscritos         // 6
            ) { flows ->
                // Extraemos los valores manualmente del array 'flows' con el tipo correcto
                val idActivo = flows[0] as? String ?: return@combine
                val configs = flows[1] as Map<String, Int>
                val asaltosDE = flows[2] as Map<String, List<Asalto>>
                val asaltosGrupos = flows[3] as Map<String, List<Asalto>>
                val inscripciones = flows[4] as Map<String, List<Usuario>>
                val todosLosArbitros = flows[5] as Map<String, List<Usuario>>

                // Lógica de ranking
                val rankingMap = RankingLogic.procesar(asaltosGrupos, inscripciones)
                val rankingActual = rankingMap[idActivo] ?: emptyList()

                // Verificación de fin de grupos
                val todosTerminados = asaltosGrupos[idActivo]?.all {
                    it.estado == EstadoAsalto.FINALIZADO
                } ?: false

                _uiState.update { it.copy(
                    compId = idActivo,
                    tamanoSeleccionado = configs[idActivo] ?: 0,
                    asaltosExistentes = asaltosDE[idActivo] ?: emptyList(),
                    rankingActual = rankingActual,
                    faseGruposTerminada = todosTerminados,
                    arbitrosDisponibles = todosLosArbitros[idActivo] ?: emptyList()
                ) }
            }.collect()
        }
    }

    fun seleccionarTamano(tamano: Int) {
        _uiState.update { it.copy(tamanoSeleccionado = tamano) }
    }

    fun generarTablon() {
        val state = uiState.value

        // 1. Validaciones básicas
        if (state.compId.isEmpty() || state.tamanoSeleccionado == 0 || state.rankingActual.isEmpty()) return

        // 2. Obtener árbitros disponibles (desde el flujo de árbitros inscritos)
        val arbitrosParaSorteo = state.arbitrosDisponibles

        // 3. Generar el cuadro con lógica "inteligente pero flexible"
        val cuadroCompleto = SeedingLogic.generarCuadroCompleto(
            tiradores = state.rankingActual.take(state.tamanoSeleccionado),
            tamano = state.tamanoSeleccionado,
            arbitrosDisponibles = arbitrosParaSorteo
        )

        // 4. Guardar configuración y asaltos en el repositorio
        FencingRepository.guardarConfigTablon(state.compId, state.tamanoSeleccionado)
        FencingRepository.generarAsaltosEliminacion(state.compId, cuadroCompleto)
    }

    fun registrarResultado(asalto: Asalto, puntosA: Int, puntosB: Int) {
        val compId = uiState.value.compId
        if (compId.isEmpty()) return

        // Llamamos al repositorio que ya tiene la lógica de avance y árbitros
        FencingRepository.registrarResultadoTablon(
            compId = compId,
            asaltoId = asalto.id,
            puntosA = puntosA,
            puntosB = puntosB
        )
    }
}