package com.example.esgrimaapp.ui.poules

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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

class PoulesViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(PoulesUIState())
    val uiState = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            combine(
                FencingRepository.idCompeticionActiva,    // 1
                FencingRepository.competiciones,          // 2
                FencingRepository.inscripciones,          // 3
                FencingRepository.arbitrosInscritos,      // 4
                FencingRepository.poulesCompeticion,      // 5
                FencingRepository.asaltosCompeticion      // 6 -> Aquí rompemos el límite de 5
            ) { flowsArray ->
                // Extraemos manualmente cada valor del array en el orden correcto
                val idActivo = flowsArray[0] as String?
                val listaComp = flowsArray[1] as List<Competicion>
                val inscritos = flowsArray[2] as Map<String, List<Usuario>>
                val arbitros = flowsArray[3] as Map<String, List<Usuario>>
                val todasLasPoules = flowsArray[4] as Map<String, List<Poule>>
                val todosLosAsaltos = flowsArray[5] as Map<String, List<Asalto>>

                val compActiva = listaComp.find { it.id == idActivo }

                // Retornamos el nuevo estado actualizado
                _uiState.update { currentState ->
                    currentState.copy(
                        hayCompeticionActiva = idActivo != null,
                        nombreCompeticion = compActiva?.nombre,
                        tiradoresInscritos = inscritos[idActivo] ?: emptyList(),
                        arbitrosInscritos = arbitros[idActivo] ?: emptyList(),
                        gruposGenerados = todasLasPoules[idActivo] ?: emptyList(),
                        todosLosAsaltos = todosLosAsaltos[idActivo] ?: emptyList()
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
        val todosLosAsaltos = mutableListOf<Asalto>()

        // 1. Repartir tiradores en grupos (equitativo)
        val gruposTiradores = List(numGrupos) { mutableListOf<Usuario>() }
        tiradoresShuffled.forEachIndexed { index, tirador ->
            gruposTiradores[index % numGrupos].add(tirador)
        }

        // Configuramos una hora de inicio base (ej. 09:00 AM)
        val horaInicioBase = 9
        val minutoInicioBase = 0
        val minutosPorAsalto = 7

        // 2. Crear cada grupo y sus combates
        gruposTiradores.forEachIndexed { i, listaTiradores ->
            // Lógica de neutralidad para el árbitro (que no sea del mismo club)
            val clubsEnGrupo = listaTiradores.map { it.club }.toSet()
            val mejorArbitro = arbitros.find { it.club !in clubsEnGrupo }
                ?: arbitros.getOrNull(i % arbitros.size.coerceAtLeast(1))

            val n = listaTiradores.size
            val pouleId = "poule_${idComp}_$i"
            val nombreGrupo = "Grupo ${('A' + i)}"
            val pistaAsignada = (i % numPistas) + 1

            nuevasPoules.add(
                Poule(
                    id = pouleId,
                    nombre = nombreGrupo,
                    pista = pistaAsignada,
                    tiradores = listaTiradores,
                    arbitroAsignado = mejorArbitro,
                    asaltosTotales = (n * (n - 1)) / 2
                )
            )

            // --- GENERACIÓN DE EMPAREJAMIENTOS (Round Robin) ---
            for (idxA in listaTiradores.indices) {
                for (idxB in idxA + 1 until listaTiradores.size) {

                    // 1. Calculamos cuántos minutos han pasado en total
                    val minutosAcumulados = (todosLosAsaltos.size * minutosPorAsalto)

                    // 2. Calculamos la hora y minuto final
                    var minutoFinal = minutoInicioBase + minutosAcumulados
                    var horaFinal = horaInicioBase + (minutoFinal / 60)
                    minutoFinal %= 60
                    horaFinal %= 24 // Para que no pase de 23:59

                    // 3. Formateamos a mano (como ya haces con la fecha)
                    val horaStr = horaFinal.toString().padStart(2, '0')
                    val minStr = minutoFinal.toString().padStart(2, '0')
                    val horaFormateada = "$horaStr:$minStr"

                    todosLosAsaltos.add(
                        Asalto(
                            id = "match_${pouleId}_${idxA}_${idxB}",
                            grupoId = nombreGrupo,
                            tiradorA = listaTiradores[idxA],
                            tiradorB = listaTiradores[idxB],
                            arbitro = mejorArbitro,
                            pista = pistaAsignada,
                            tocadosA = 0,
                            tocadosB = 0,
                            estado = EstadoAsalto.PROGRAMADO,
                            horaEstimada = horaFormateada // "09:00", "09:07", etc.
                        )
                    )
                }
            }
        }

        // 3. Persistencia en el Repositorio
        FencingRepository.guardarPoules(idComp, nuevasPoules)
        FencingRepository.guardarAsaltos(idComp, todosLosAsaltos)
    }
}