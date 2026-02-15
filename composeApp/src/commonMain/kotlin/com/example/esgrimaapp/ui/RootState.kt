package com.example.esgrimaapp.ui

import com.example.esgrimaapp.data.Competicion
import com.example.esgrimaapp.data.Poule
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.competicion.CompeticionUIState
import com.example.esgrimaapp.ui.tiradores.TiradoresUIState
import com.example.esgrimaapp.ui.usuarios.UsuarioUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Este objeto es único para toda la App
object FencingRepository {
    // --- Usuarios Globales ---
    private val _usuariosGlobales = MutableStateFlow<List<Usuario>>(emptyList())
    val usuariosGlobales = _usuariosGlobales.asStateFlow()

    // --- Competiciones ---
    private val _competiciones = MutableStateFlow<List<Competicion>>(emptyList())
    val competiciones = _competiciones.asStateFlow()

    // --- ID de Competición Activa ---
    private val _idCompeticionActiva = MutableStateFlow<String?>(null)
    val idCompeticionActiva = _idCompeticionActiva.asStateFlow()

    // --- Relación: Competición -> Tiradores Inscritos ---
    private val _inscripciones = MutableStateFlow<Map<String, List<Usuario>>>(emptyMap())
    val inscripciones = _inscripciones.asStateFlow()

    // Relación: Competición -> Árbitros Inscritos
    private val _arbitrosInscritos = MutableStateFlow<Map<String, List<Usuario>>>(emptyMap())
    val arbitrosInscritos = _arbitrosInscritos.asStateFlow()

    private val _poulesCompeticion = MutableStateFlow<Map<String, List<Poule>>>(emptyMap())
    val poulesCompeticion = _poulesCompeticion.asStateFlow()

    // Funciones de Usuarios
    fun agregarUsuario(usuario: Usuario) { _usuariosGlobales.update { it + usuario } }

    fun actualizarUsuario(editado: Usuario) {
        _usuariosGlobales.update { lista ->
            lista.map { if (it.numeroFederacion == editado.numeroFederacion) editado else it }
        }
    }

    fun eliminarUsuario(numFede: String) {
        _usuariosGlobales.update { lista -> lista.filterNot { it.numeroFederacion == numFede } }
    }

    // Funciones de Competiciones
    fun agregarCompeticion(comp: Competicion) { _competiciones.update { it + comp } }

    fun setCompeticionActiva(id: String?) { _idCompeticionActiva.value = id }

    fun eliminarCompeticion(id: String) {
        _competiciones.update { it.filterNot { comp -> comp.id == id } }
        // Si borramos la activa, ponemos el ID a null
        if (_idCompeticionActiva.value == id) _idCompeticionActiva.value = null
        // También limpiamos sus inscripciones
        _inscripciones.update { it - id }
    }

    // Funciones de Inscripción
    fun inscribirTirador(compId: String, usuario: Usuario) {
        _inscripciones.update { actual ->
            val listaActual = actual[compId] ?: emptyList()
            if (!listaActual.any { it.numeroFederacion == usuario.numeroFederacion }) {
                actual + (compId to (listaActual + usuario))
            } else actual
        }
    }

    fun desapuntarTirador(compId: String, numFede: String) {
        _inscripciones.update { actual ->
            val listaActual = actual[compId] ?: emptyList()
            actual + (compId to listaActual.filterNot { it.numeroFederacion == numFede })
        }
    }

    fun inscribirArbitro(compId: String, usuario: Usuario) {
        _arbitrosInscritos.update { actual ->
            val lista = actual[compId] ?: emptyList()
            actual + (compId to (lista + usuario))
        }
    }

    fun desapuntarArbitro(compId: String, numFede: String) {
        _arbitrosInscritos.update { actual ->
            val lista = actual[compId] ?: emptyList()
            actual + (compId to lista.filterNot { it.numeroFederacion == numFede })
        }
    }

    fun guardarPoules(compId: String, lista: List<Poule>) {
        _poulesCompeticion.update { it + (compId to lista) }
    }

    fun reiniciarPoules(compId: String) {
        _poulesCompeticion.update { it - compId }
    }
}