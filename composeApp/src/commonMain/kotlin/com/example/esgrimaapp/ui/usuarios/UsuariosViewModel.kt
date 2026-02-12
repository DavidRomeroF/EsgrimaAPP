package com.example.esgrimaapp.ui.usuarios

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.esgrimaapp.data.Competicion
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.tiradores.TiradoresUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import kotlin.time.Clock

class UsuariosViewModel: ScreenModel {
    private val _uiState = MutableStateFlow(UsuarioUIState())
    val uiState = _uiState.asStateFlow()

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

        // 3. Persistencia (Simulada aquí, pero deberías llamar a tu repositorio/DB)
        // Recuerda que cada administrador debe tener sus propios datos
        _uiState.update { currentState ->
            currentState.copy(
                listaUsuarios = currentState.listaUsuarios + nuevo,
                mostrarFormulario = false // Cerramos el formulario tras éxito
            )
        }

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
        // 1. Filtramos la lista eliminando el usuario con ese número de federado
        _uiState.update { currentState ->
            val nuevaLista = currentState.listaUsuarios.filterNot { it.numeroFederacion == numFede }
            currentState.copy(listaUsuarios = nuevaLista)
        }

        // 2. Aquí llamarías a tu Repositorio para borrarlo de la base de datos:
        // viewModelScope.launch {
        //    repository.deleteUser(numFede, adminId = currentAdminId)
        // }
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
        _uiState.update { estado ->
            val listaActualizada = estado.listaUsuarios.map {
                if (it.numeroFederacion == numFede) {
                    it.copy(
                        nombre = estado.editNombre,
                        club = estado.editClub,
                        esArbitro = estado.editEsArbitro,
                        especialidades = estado.editEspecialidades.toList()
                    )
                } else it
            }
            estado.copy(listaUsuarios = listaActualizada, idUsuarioEditando = null)
        }
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