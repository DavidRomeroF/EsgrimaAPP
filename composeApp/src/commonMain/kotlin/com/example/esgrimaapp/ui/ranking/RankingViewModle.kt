package com.example.esgrimaapp.ui.ranking

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.ui.FencingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RankingViewModel : ScreenModel {
    private val _ranking = MutableStateFlow<List<EstadisticasTirador>>(emptyList())
    val ranking = _ranking.asStateFlow()

    init {
        calcularRanking()
    }

    fun calcularRanking() {
        screenModelScope.launch {
            // Obtenemos los asaltos y tiradores del repositorio
            combine(
                FencingRepository.asaltosCompeticion,
                FencingRepository.idCompeticionActiva,
                FencingRepository.inscripciones
            ) { todosLosAsaltos, idActivo, todosLosTiradores ->
                val asaltosActuales = todosLosAsaltos[idActivo] ?: return@combine
                val tiradoresActuales = todosLosTiradores[idActivo] ?: return@combine

                // Inicializamos mapa de estadísticas
                val statsMap = tiradoresActuales.associateWith { EstadisticasTirador(it) }.toMutableMap()

                // Procesamos solo asaltos finalizados
                asaltosActuales.filter { it.estado == EstadoAsalto.FINALIZADO }.forEach { asalto ->
                    val sA = statsMap[asalto.tiradorA]!!
                    val sB = statsMap[asalto.tiradorB]!!

                    statsMap[asalto.tiradorA] = sA.copy(
                        victorias = sA.victorias + if (asalto.tocadosA > asalto.tocadosB) 1 else 0,
                        derrotas = sA.derrotas + if (asalto.tocadosA < asalto.tocadosB) 1 else 0,
                        tocadosDados = sA.tocadosDados + asalto.tocadosA,
                        tocadosRecibidos = sA.tocadosRecibidos + asalto.tocadosB
                    )

                    statsMap[asalto.tiradorB] = sB.copy(
                        victorias = sB.victorias + if (asalto.tocadosB > asalto.tocadosA) 1 else 0,
                        derrotas = sB.derrotas + if (asalto.tocadosB < asalto.tocadosA) 1 else 0,
                        tocadosDados = sB.tocadosDados + asalto.tocadosB,
                        tocadosRecibidos = sB.tocadosRecibidos + asalto.tocadosA
                    )
                }

                // Ordenamos por índice de mayor a menor
                _ranking.value = statsMap.values.sortedByDescending { it.indice }
            }.collect()
        }
    }
}