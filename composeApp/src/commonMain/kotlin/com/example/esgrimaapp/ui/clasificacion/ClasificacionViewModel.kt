package com.example.esgrimaapp.ui.clasificacion

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.EstadoAsalto
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
    private val _uiState = MutableStateFlow(ClasifiacionUIState()) // Asegúrate de que el nombre del State sea correcto
    val uiState = _uiState.asStateFlow()

    init {
        observarDatos()
    }

    private fun observarDatos() {
        screenModelScope.launch {
            // Observamos los StateFlows del repositorio
            combine(
                FencingRepository.idCompeticionActiva,
                FencingRepository.tablonConfig,
                FencingRepository.asaltosEliminacion,
                FencingRepository.asaltosCompeticion,
                FencingRepository.inscripciones
            ) { id, configs, asaltosDE, asaltosGrupos, inscripciones ->

                val idActivo = id ?: return@combine

                // Calculamos el ranking actual en base a los datos actuales
                val rankingMap = RankingLogic.procesar(asaltosGrupos, inscripciones)
                val rankingActual = rankingMap[idActivo] ?: emptyList()

                // Verificamos si todos los asaltos de grupo están FINALIZADOS
                val todosTerminados = asaltosGrupos[idActivo]?.all {
                    it.estado == EstadoAsalto.FINALIZADO
                } ?: false

                _uiState.update { it.copy(
                    compId = idActivo,
                    tamanoSeleccionado = configs[idActivo] ?: 0,
                    asaltosExistentes = asaltosDE[idActivo] ?: emptyList(),
                    rankingActual = rankingActual,
                    faseGruposTerminada = todosTerminados
                ) }
            }.collect()
        }
    }

    fun seleccionarTamano(tamano: Int) {
        _uiState.update { it.copy(tamanoSeleccionado = tamano) }
    }

    // Ejemplo de cómo queda tu llamada final en el ViewModel
    fun generarTablon() {
        val state = uiState.value

        // 1. Validaciones de seguridad
        if (state.compId.isEmpty()) return
        if (state.tamanoSeleccionado == 0) return
        if (state.rankingActual.isEmpty()) return

        // 2. Obtenemos los clasificados según el tamaño elegido (T8, T16, T32)
        val clasificados = state.rankingActual.take(state.tamanoSeleccionado)

        // 3. Usamos la nueva función que genera TODOS los asaltos del tirón (el árbol completo)
        // Esto es lo que evitará que la pantalla se quede en blanco o con huecos raros
        val cuadroCompleto = SeedingLogic.generarCuadroCompleto(
            tiradores = clasificados,
            tamano = state.tamanoSeleccionado
        )

        // 4. Persistencia en la base de datos
        // Guardamos la configuración y los asaltos generados
        FencingRepository.guardarConfigTablon(state.compId, state.tamanoSeleccionado)
        FencingRepository.generarAsaltosEliminacion(state.compId, cuadroCompleto)

        // Opcional: Podrías marcar la fase de grupos como cerrada aquí si no se ha hecho antes
    }
    // En ClasificacionViewModel
    fun registrarResultado(asalto: Asalto, puntosA: Int, puntosB: Int) {
        val compId = uiState.value.compId
        if (compId.isEmpty()) return

        // Llamamos al repositorio que ya configuramos con la lógica de avance
        FencingRepository.registrarResultadoTablon(
            compId = compId,
            asaltoId = asalto.id,
            puntosA = puntosA,
            puntosB = puntosB
        )
    }
}