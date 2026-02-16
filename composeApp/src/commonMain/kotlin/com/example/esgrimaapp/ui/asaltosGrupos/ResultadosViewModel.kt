package com.example.esgrimaapp.ui.asaltosGrupos

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.FencingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultadosViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(ResultadosUIState())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                FencingRepository.idCompeticionActiva,
                FencingRepository.poulesCompeticion,
                FencingRepository.asaltosCompeticion
            ) { idActivo, poules, asaltos ->
                val grupos = poules[idActivo] ?: emptyList()
                val listaAsaltos = asaltos[idActivo] ?: emptyList()

                _uiState.update { it.copy(
                    hayGrupos = grupos.isNotEmpty(),
                    asaltosPorGrupo = listaAsaltos.groupBy { a -> a.grupoId }
                ) }
            }.collect()
        }
    }

    fun setFase(esGrupos: Boolean) {
        _uiState.update { it.copy(faseGruposActiva = esGrupos) }
    }

    fun abrirEditor(asalto: Asalto?) {
        _uiState.update { it.copy(asaltoParaEditar = asalto) }
    }

    fun registrarPuntos(puntosA: Int, puntosB: Int, estado: EstadoAsalto) {
        val idComp = FencingRepository.idCompeticionActiva.value ?: return
        val asalto = uiState.value.asaltoParaEditar ?: return

        FencingRepository.actualizarResultadoAsalto(idComp, asalto.id, puntosA, puntosB, estado)
        _uiState.update { it.copy(asaltoParaEditar = null) }
    }

    fun esArbitroValido(arbitro: Usuario, asalto: Asalto): Boolean {
        // Si el asalto está vacío, no hay conflicto
        if (asalto.tiradorA.nombre == "---" && asalto.tiradorB.nombre == "---") return true

        // Comprobamos si el club del árbitro coincide con alguno de los tiradores
        val clubArbitro = arbitro.club ?: return true // Si el árbitro no tiene club, no hay conflicto

        return clubArbitro != asalto.tiradorA.club && clubArbitro != asalto.tiradorB.club
    }
}