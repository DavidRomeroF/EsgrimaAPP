package com.example.esgrimaapp.ui.usuarios

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.esgrimaapp.data.Competicion
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.FencingRepository
import com.example.esgrimaapp.ui.tiradores.TiradoresUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.Clock

class UsuariosViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(UsuarioUIState())
    val uiState = _uiState.asStateFlow()

    init {
        // IMPORTANTE: Nos suscribimos al repositorio para que la lista
        // de la tabla se actualice sola si añadimos/borramos algo
        screenModelScope.launch {
            FencingRepository.usuariosGlobales.collect { lista ->
                _uiState.update { it.copy(listaUsuarios = lista) }
            }
        }
    }

    fun toggleFormulario() {
        _uiState.update { currentState ->
            currentState.copy(mostrarFormulario = !currentState.mostrarFormulario)
        }
    }

    fun onNombreChange(nuevoNombre: String) {
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun onClubChange(nuevoClub: String) {
        _uiState.update { it.copy(club = nuevoClub) }
    }

    fun onContrasenyaChange(nuevaContrasenya: String) {
        _uiState.update { it.copy(contrasenya = nuevaContrasenya) }
    }

    fun onRepContrasenyaChange(nuevaRep: String) {
        _uiState.update { it.copy(repContrasenya = nuevaRep) }
    }

    fun onNumeroFederacionChange(nuevoNumero: String) {
        // Siguiendo tu regla de negocio: "Cada persona se registra una sola vez
        // con su número de federado único"
        _uiState.update { it.copy(numeroFederacion = nuevoNumero) }
    }


    fun crearNuevoUsuario() {
        val estado = uiState.value

        // 1. Validaciones de seguridad y lógica
        val camposVacios = estado.nombre.isBlank() || estado.numeroFederacion.isBlank()
        val contrasenyasNoCoinciden = estado.contrasenya != estado.repContrasenya
        val arbitroSinEspecialidad = estado.esArbitro && estado.especialidades.isEmpty()

        if (camposVacios || contrasenyasNoCoinciden || arbitroSinEspecialidad) {
            // Opcional: _uiState.update { it.copy(mensajeError = "Revisa los datos") }
            return
        }

        // 2. Crear el objeto Usuario (incluyendo la lógica de árbitro)
        val nuevo = Usuario(
            numeroFederacion = estado.numeroFederacion,
            nombre = estado.nombre,
            club = estado.club,
            contrasenya = estado.contrasenya,
            // Si tu data class Usuario soporta estos campos:
            esArbitro = estado.esArbitro,
            especialidades = estado.especialidades.toList()
        )

        FencingRepository.agregarUsuario(nuevo)

        // 4. Limpiar datos sensibles
        limpiarFormulario()
    }

    fun limpiarFormulario() {
        _uiState.update { it.copy(
            nombre = "",
            numeroFederacion = "",
            club = "",
            contrasenya = "",
            repContrasenya = "",
            esArbitro = false,
            especialidades = emptySet(),
            mostrarFormulario = false
        ) }
    }

    fun onEsArbitroChange(check: Boolean) {
        _uiState.update { it.copy(esArbitro = check) }
    }

    fun onEspecialidadToggle(especialidad: String) {
        _uiState.update { estado ->
            val nuevasEspecialidades = if (estado.especialidades.contains(especialidad)) {
                estado.especialidades - especialidad // Quitar si ya estaba
            } else {
                estado.especialidades + especialidad // Añadir si no estaba
            }
            estado.copy(especialidades = nuevasEspecialidades)
        }
    }
    fun eliminarUsuario(numFede: String) {
        FencingRepository.eliminarUsuario(numFede)
    }

    fun iniciarEdicion(usuario: Usuario) {
        _uiState.update { it.copy(
            idUsuarioEditando = usuario.numeroFederacion,
            editNombre = usuario.nombre,
            editClub = usuario.club,
            editEsArbitro = usuario.esArbitro,
            editEspecialidades = usuario.especialidades.toSet()
        ) }
    }

    fun cancelarEdicion() {
        _uiState.update { it.copy(idUsuarioEditando = null) }
    }

    fun guardarEdicion(numFede: String) {
        val estado = uiState.value
        val usuarioEditado = Usuario(
            numeroFederacion = numFede,
            nombre = estado.editNombre,
            club = estado.editClub,
            esArbitro = estado.editEsArbitro,
            especialidades = estado.editEspecialidades.toList()
        )

        // CAMBIO CLAVE: Actualizamos en el repositorio
        FencingRepository.actualizarUsuario(usuarioEditado)

        _uiState.update { it.copy(idUsuarioEditando = null) }
    }

    fun onEditNombre(nuevo: String) {
        _uiState.update { it.copy(editNombre = nuevo) }
    }

    fun onEditClub(nuevo: String) {
        _uiState.update { it.copy(editClub = nuevo) }
    }

    fun onEditEsArbitro(check: Boolean) {
        _uiState.update { it.copy(editEsArbitro = check) }
    }

    fun onEditEspecialidadToggle(espec: String) {
        _uiState.update { estado ->
            val nuevas = if (estado.editEspecialidades.contains(espec)) {
                estado.editEspecialidades - espec
            } else {
                estado.editEspecialidades + espec
            }
            estado.copy(editEspecialidades = nuevas)
        }
    }
}