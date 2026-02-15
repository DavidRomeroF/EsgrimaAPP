package com.example.esgrimaapp.ui.ranking

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.ui.FencingRepository
import com.example.esgrimaapp.utilits.RankingLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RankingViewModel : ScreenModel {
    private val _ranking = MutableStateFlow<List<EstadisticasTirador>>(emptyList())
    val ranking = _ranking.asStateFlow()

    init {
        screenModelScope.launch {
            // Especificamos el tipo de salida de combine: <List<EstadisticasTirador>>
            combine(
                FencingRepository.asaltosCompeticion,
                FencingRepository.inscripciones,
                FencingRepository.idCompeticionActiva
            ) { asaltos, inscripciones, idActivo ->

                if (idActivo == null) return@combine emptyList<EstadisticasTirador>()

                // Llamamos a la lógica y forzamos la recolección del primer valor emitido
                // Aquí es donde el compilador solía fallar al inferir 'R'
                val rankingProcesado = RankingLogic.procesar(asaltos, inscripciones)[idActivo]

                rankingProcesado ?: emptyList<EstadisticasTirador>()

            }.collect { listaCalculada ->
                _ranking.value = listaCalculada
            }
        }
    }
}