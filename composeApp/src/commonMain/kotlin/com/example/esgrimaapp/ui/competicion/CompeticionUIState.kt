package com.example.esgrimaapp.ui.competicion

import com.example.esgrimaapp.data.Competicion


data class CompeticionUIState(
    // Control de visibilidad del formulario
    val mostrarFormulario: Boolean = false,

    // Datos del formulario
    val nombre: String = "",
    val entidadOrganizadora: String = "",
    val lugar: String = "",

    // Gestión de la Fecha
    val fechaTexto: String = "",        // Lo que se ve: "12/02/2026"
    val fechaParaBD: Long? = null,      // Lo que se guarda en la DB (Date/Long)
    val mostrarDatePicker: Boolean = false,

    // Gestión del Arma (Dropdown)
    val arma: String = "Espada",        // Valor inicial por defecto
    val menuArmaExpandido: Boolean = false,

    // Estado de validación (Opcional: para habilitar el botón de guardar)
    val esFormularioValido: Boolean = false,

    val listaCompeticiones: List<Competicion> = emptyList(),
    val idCompeticionActiva: String? = null, // Controla cuál se resalta
)