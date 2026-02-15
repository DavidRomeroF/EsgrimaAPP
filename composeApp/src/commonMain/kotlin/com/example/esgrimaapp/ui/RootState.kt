package com.example.esgrimaapp.ui

import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.Competicion
import com.example.esgrimaapp.data.EstadoAsalto
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

    private val _asaltosCompeticion = MutableStateFlow<Map<String, List<Asalto>>>(emptyMap())
    val asaltosCompeticion = _asaltosCompeticion.asStateFlow()

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

    fun guardarAsaltos(compId: String, lista: List<Asalto>) {
        _asaltosCompeticion.update { it + (compId to lista) }
    }

    fun actualizarResultadoAsalto(compId: String, asaltoId: String, nuevoA: Int, nuevoB: Int, nuevoEstado: EstadoAsalto) {
        val asaltosActuales = _asaltosCompeticion.value[compId] ?: return
        val listaActualizada = asaltosActuales.map {
            if (it.id == asaltoId) it.copy(tocadosA = nuevoA, tocadosB = nuevoB, estado = nuevoEstado)
            else it
        }
        _asaltosCompeticion.update { it + (compId to listaActualizada) }
    }

    fun cargarDatosPrueba() {
        val idComp = "comp_demo_2026"

        // 1. Crear Competición con los nuevos campos [entidad, fecha, lugar, arma]
        val compDemo = Competicion(
            id = idComp,
            nombre = "I Torneo Clasificatorio 2026",
            entidad = "Federación Madrileña de Esgrima",
            fecha = "15/02/2026",
            lugar = "Polideportivo Municipal, Madrid",
            arma = "Espada Masculina"
        )

        _idCompeticionActiva.value = idComp
        _competiciones.update { it + compDemo }

        // 2. Definir Árbitros (con el campo esArbitro y especialidades)
        val arbitrosDemo = listOf(
            Usuario(
                nombre = "Carlos Juez",
                club = "C.E. Madrid",
                esArbitro = true,
                especialidades = listOf("Espada", "Florete")
            ),
            Usuario(
                nombre = "Ana Referí",
                club = "S.A. Granollers",
                esArbitro = true,
                especialidades = listOf("Espada")
            )
        )
        _arbitrosInscritos.update { it + (idComp to arbitrosDemo) }

        // 3. Definir Tiradores (6 para hacer 2 grupos de 3)
        val tiradoresDemo = listOf(
            Usuario("Pablo", "C.E. Madrid"),
            Usuario("Marta", "S.A. Granollers"),
            Usuario("Luis", "VCE Valladolid"),
            Usuario("Elena", "C.E. Madrid"),
            Usuario("Marcos", "S.A. Granollers"),
            Usuario("Lucía", "VCE Valladolid")
        )
        _inscripciones.update { it + (idComp to tiradoresDemo) }

    }
}

