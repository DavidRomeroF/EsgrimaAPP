package com.example.esgrimaapp.ui.usuarios

import com.example.esgrimaapp.data.Competicion
import com.example.esgrimaapp.data.Usuario

data class UsuarioUIState(
    // Control de visibilidad del formulario
    val mostrarFormulario: Boolean = false,
    // Datos del formulario de registro
    val nombre: String = "",
    val club: String = "",
    val numeroFederacion: String = "",
    val contrasenya: String = "",
    val repContrasenya: String = "",

    // Gestión de Árbitros (según capturas)
    val esArbitro: Boolean = false,
    val especialidades: Set<String> = emptySet(), // Ejemplo: "Espada", "Florete", "Sable"

    // Datos cargados desde la BD (filtrados por tu Admin actual)
    val listaUsuarios: List<Usuario> = emptyList(),

    // Estado de carga y errores
    val cargando: Boolean = false,
    val mensajeError: String? = null,

    // En tu UsuarioUIState
    val idUsuarioEditando: String? = null, // numFede del usuario en edición
    val editNombre: String = "",
    val editClub: String = "",
    val editEsArbitro: Boolean = false,
    val editEspecialidades: Set<String> = emptySet()
)