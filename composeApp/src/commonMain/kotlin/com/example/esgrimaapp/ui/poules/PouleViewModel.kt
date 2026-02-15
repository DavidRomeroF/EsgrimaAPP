package com.example.esgrimaapp.ui.poules

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Poule
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.FencingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PoulesViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(PoulesUIState())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                FencingRepository.idCompeticionActiva,
                FencingRepository.competiciones,
                FencingRepository.inscripciones,
                FencingRepository.arbitrosInscritos,
                FencingRepository.poulesCompeticion
            ) { idActivo, listaComp, inscritos, arbitros, todasLasPoules ->
                val compActiva = listaComp.find { it.id == idActivo }
                _uiState.update { currentState ->
                    currentState.copy(
                        hayCompeticionActiva = idActivo != null,
                        nombreCompeticion = compActiva?.nombre,
                        tiradoresInscritos = inscritos[idActivo] ?: emptyList(),
                        arbitrosInscritos = arbitros[idActivo] ?: emptyList(),
                        gruposGenerados = todasLasPoules[idActivo] ?: emptyList()
                    )
                }
            }.collect()
        }
    }

    // --- Funciones para los TextFields ---
    fun onGruposChange(nuevo: String) {
        if (nuevo.isEmpty() || nuevo.all { it.isDigit() }) {
            _uiState.update { it.copy(cantidadGrupos = nuevo) }
        }
    }

    fun onPistasChange(nuevo: String) {
        if (nuevo.isEmpty() || nuevo.all { it.isDigit() }) {
            _uiState.update { it.copy(cantidadPistas = nuevo) }
        }
    }

    fun reiniciarGrupos() {
        val idComp = FencingRepository.idCompeticionActiva.value ?: return
        FencingRepository.reiniciarPoules(idComp)
    }

    // --- Lógica de Generación ---
    fun generarGrupos() {
        val idComp = FencingRepository.idCompeticionActiva.value ?: return
        val numGrupos = uiState.value.cantidadGrupos.toIntOrNull() ?: return
        val numPistas = uiState.value.cantidadPistas.toIntOrNull() ?: 1

        val tiradoresShuffled = uiState.value.tiradoresInscritos.shuffled()
        val arbitros = uiState.value.arbitrosInscritos.shuffled()

        val nuevasPoules = mutableListOf<Poule>()
        val gruposTiradores = List(numGrupos) { mutableListOf<Usuario>() }

        // Repartir tiradores
        tiradoresShuffled.forEachIndexed { index, tirador ->
            gruposTiradores[index % numGrupos].add(tirador)
        }

        // Asignar pistas y árbitros
        gruposTiradores.forEachIndexed { i, listaTiradores ->
            val clubsEnGrupo = listaTiradores.map { it.club }.toSet()
            // Intentamos buscar árbitro que no sea de esos clubs
            val mejorArbitro = arbitros.find { it.club !in clubsEnGrupo }
                ?: arbitros.getOrNull(i % arbitros.size.coerceAtLeast(1))

            val n = listaTiradores.size
            nuevasPoules.add(
                Poule(
                    id = "poule_${idComp}_$i",
                    nombre = "Grupo ${('A' + i)}",
                    pista = (i % numPistas) + 1,
                    tiradores = listaTiradores,
                    arbitroAsignado = mejorArbitro,
                    asaltosTotales = (n * (n - 1)) / 2 // Fórmula de liguilla
                )
            )
        }

        FencingRepository.guardarPoules(idComp, nuevasPoules)
    }
}