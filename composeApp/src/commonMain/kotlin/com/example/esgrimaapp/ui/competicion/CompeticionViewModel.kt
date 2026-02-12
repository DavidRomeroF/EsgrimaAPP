package com.example.esgrimaapp.ui.competicion

import cafe.adriel.voyager.core.model.ScreenModel
import com.example.esgrimaapp.data.Competicion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.random.Random
import kotlin.time.Clock

class CompeticionViewModel: ScreenModel {
    private val _uiState = MutableStateFlow(CompeticionUIState())
    val uiState = _uiState.asStateFlow()

    fun toggleFormulario() {
        _uiState.update { currentState ->
            currentState.copy(mostrarFormulario = !currentState.mostrarFormulario)
        }
    }

    // --- En tu ViewModel ---

    // 1. Ya la tienes
    fun onNombreChange(nuevoNombre: String) {
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    // 2. Ya la tienes
    fun onOrganizadorChange(nuevoOrg: String) {
        _uiState.update { it.copy(entidadOrganizadora = nuevoOrg) }
    }

    // 3. FALTA: Para el campo "Lugar"
    fun onLugarChange(nuevoLugar: String) {
        _uiState.update { it.copy(lugar = nuevoLugar) }
    }

    // 4. FALTA: Para el "Arma" (desde el Dropdown)
    fun onArmaChange(nuevaArma: String) {
        _uiState.update { it.copy(arma = nuevaArma, menuArmaExpandido = false) }
    }


    fun onFechaChange(milis: Long?) {
        if (milis == null) return

        // Convertimos milisegundos a días totales desde la época Unix (1 de enero de 1970)
        val totalSegundos = milis / 1000
        val totalDias = (totalSegundos / 86400).toInt()

        // Algoritmo simplificado para obtener Fecha desde Días Unix
        var l = totalDias + 68569 + 2440588
        val n = (4 * l) / 146097
        l = l - (146097 * n + 3) / 4
        val i = (4000 * (l + 1)) / 1461001
        l = l - (1461 * i) / 4 + 31
        val j = (80 * l) / 2447
        val dia = l - (2447 * j) / 80
        l = j / 11
        val mes = j + 2 - (12 * l)
        val anio = 100 * (n - 49) + i + l

        // Formateo manual con ceros a la izquierda
        val diaStr = dia.toString().padStart(2, '0')
        val mesStr = mes.toString().padStart(2, '0')
        val fechaFormateada = "$diaStr/$mesStr/$anio"

        _uiState.update { it.copy(
            fechaParaBD = milis,
            fechaTexto = fechaFormateada
        ) }
    }

    // Extra: Para abrir/cerrar el menú de armas y el calendario
    fun toggleMenuArma() {
        _uiState.update { it.copy(menuArmaExpandido = !it.menuArmaExpandido) }
    }

    fun toggleDatePicker() {
        _uiState.update { it.copy(mostrarDatePicker = !it.mostrarDatePicker) }
    }

    fun crearNuevaCompeticion() {
        // 1. Validamos que los campos mínimos no estén vacíos
        if (uiState.value.nombre.isBlank() || uiState.value.fechaTexto.isBlank()) {
            // Podrías manejar un error aquí si quisieras
            return
        }
        val nuevoId = "comp_${Clock.System.now().toEpochMilliseconds()}"

        // 2. Creamos el nuevo objeto de competición
        val nueva = Competicion(
            id = "comp_${Random.nextLong()}", // Opción rápida sin librerías externas
            nombre = uiState.value.nombre,
            entidad = uiState.value.entidadOrganizadora,
            fecha = uiState.value.fechaTexto,
            lugar = uiState.value.lugar,
            arma = uiState.value.arma
        )

        // 3. Actualizamos la lista y limpiamos el formulario
        _uiState.update { currentState ->
            currentState.copy(
                listaCompeticiones = currentState.listaCompeticiones + nueva,
                // Al crearla, podemos hacer que sea la activa automáticamente
                idCompeticionActiva = nueva.id
            )
        }

        // 4. Resetear los campos para que el formulario quede vacío
        limpiarFormulario()
    }

    fun limpiarFormulario() {
        _uiState.update { it.copy(
            nombre = "",
            entidadOrganizadora = "",
            fechaTexto = "",
            fechaParaBD = null,
            lugar = "",
            arma = "Espada", // O tu valor por defecto
            mostrarFormulario = false // Cerramos el formulario al limpiar
        ) }
    }

    fun seleccionarCompeticion(id: String) {
        // Solo cambiamos datos, no llamamos a funciones de UI
        _uiState.update { it.copy(idCompeticionActiva = id) }
    }

    fun eliminarCompeticion(id: String) {
        // Lógica para filtrar la lista y actualizar el estado
        _uiState.update { currentState ->
            currentState.copy(
                listaCompeticiones = currentState.listaCompeticiones.filter { it.id != id },
                idCompeticionActiva = if (currentState.idCompeticionActiva == id) null else currentState.idCompeticionActiva
            )
        }
    }
}