package com.example.esgrimaapp.ui.competicion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.aprendepalabras.ui.theme.CartaArbitros
import com.example.aprendepalabras.ui.theme.CartaAsaltosDeGrupos
import com.example.aprendepalabras.ui.theme.CartaEliminatorias
import com.example.aprendepalabras.ui.theme.CartaGrupos
import com.example.aprendepalabras.ui.theme.CartaTiradores
import com.example.esgrimaapp.data.Competicion
import com.example.esgrimaapp.ui.home.StatCard

class CompeticionScreen : Screen {
    @Composable
    override fun Content() {
        // En KMP con Voyager, esto es lo que evita el crash del Factory
        val viewModel = rememberScreenModel { CompeticionViewModel() }

        // Pasamos el estado recolectado para que el Layout sea "puro"
        val uiState by viewModel.uiState.collectAsState()

        // El Scaffold asegura que el fondo y el área de contenido se gestionen bien
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                CompeticionLayout(uiState, viewModel)
            }
        }
    }
}

@Composable
fun CompeticionLayout(
    uiState: CompeticionUIState,
    viewModel: CompeticionViewModel
) {
    if (uiState.mostrarDatePicker) {
        SelectorFechaKMP(
            onFechaSeleccionada = { milis ->
                viewModel.onFechaChange(milis) // 1. Guarda la fecha
                viewModel.toggleDatePicker()   // 2. CIERRA el diálogo
            },
            onDismiss = { viewModel.toggleDatePicker() }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // Importante para que ocupe toda la pantalla
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Añade espacio entre bloques automáticamente
    ) {
        // 1. Encabezado
        item {
            Column {
                Text(
                    text = "Gestión de Competiciones",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Crear y gestionar las competiciones de esgrima")
            }
        }

        // 2. Botón de acción (Nueva Competición)
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                OutlinedButton(
                    onClick = { viewModel.toggleFormulario() },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CartaTiradores),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CartaTiradores,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Nueva Competición",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 3. Formulario (Solo si está activo)
        item {
            // El formulario ya gestiona su visibilidad interna con uiState.mostrarFormulario
            FormularioCreacionCompeticion(uiState.mostrarFormulario, uiState, viewModel)
        }

        // 4. Listado de competiciones existentes
        item {
            Text(
                text = "Competiciones Guardadas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item {
            ListaCompeticiones(
                competiciones = uiState.listaCompeticiones, // La lista de objetos Competicion
                idSeleccionado = uiState.idCompeticionActiva, // El ID que brilla en azul
                onSeleccionar = { id -> viewModel.seleccionarCompeticion(id) },
                onEliminar = { id -> viewModel.eliminarCompeticion(id) }
            )
        }
    }
}

@Composable
fun FormularioCreacionCompeticion(mostrar: Boolean, uiState: CompeticionUIState,viewModel: CompeticionViewModel){

    if(mostrar){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                // Añadimos el fondo y el borde aquí
                .background(
                    color = Color.White, // O un color gris muy suave
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant, // Borde sutil
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(20.dp), // Padding interno para que el contenido no pegue al borde
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .heightIn(max = 2000.dp) // Dale suficiente margen para el formulario
                    .fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    userScrollEnabled = false
                ) {
                    item {
                        CampoTexto(
                            "Nombre de la Competición",
                            uiState.nombre
                        ) { viewModel.onNombreChange(it) }
                    }
                    item {
                        CampoTexto(
                            "Entidad Organizadora",
                            uiState.entidadOrganizadora
                        ) { viewModel.onOrganizadorChange(it) }
                    }
                    item {
                        CampoFecha("Fecha", uiState.fechaTexto) { viewModel.toggleDatePicker() }
                    }
                    item {
                        CampoTexto("Lugar", uiState.lugar) { viewModel.onLugarChange(it) }
                    }
                    item {
                        SelectorArma(
                            "Arma",
                            uiState.arma,
                            uiState.menuArmaExpandido,
                            { viewModel.toggleMenuArma() }) {
                            viewModel.onArmaChange(it)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End) // Botones a la derecha
            ) {
                TextButton(
                    onClick = { viewModel.toggleFormulario()
                              viewModel.limpiarFormulario()},
                    modifier = Modifier
                        .width(150.dp) // Puedes ajustar el ancho o quitarlo para que ocupe solo el texto
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray // Esto pone el texto en gris
                    )
                ) {
                    Text(
                        text = "Cancelar",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                OutlinedButton(
                    onClick = { viewModel.crearNuevaCompeticion() },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CartaTiradores),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CartaTiradores,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Crear Competición",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ContenedorCampo(label: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        content()
    }
}
@Composable
fun CampoTexto(label: String, value: String, onValueChange: (String) -> Unit) {
    ContenedorCampo(label) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Escribe aquí...") },
            shape = RoundedCornerShape(10.dp),
            singleLine = true
        )
    }
}

@Composable
fun CampoFecha(label: String, fechaTexto: String, onClick: () -> Unit) {
    ContenedorCampo(label) {
        OutlinedTextField(
            value = fechaTexto,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Selecciona una fecha...") },
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF6750A4))
                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorArma(label: String, armaSeleccionada: String, expandido: Boolean, onExpandChange: () -> Unit, onArmaSelected: (String) -> Unit) {
    val opciones = listOf("Espada", "Florete", "Sable")
    ContenedorCampo(label) {
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { onExpandChange() },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = armaSeleccionada,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) }
            )
            ExposedDropdownMenu(expanded = expandido, onDismissRequest = { onExpandChange() }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = { onArmaSelected(opcion) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorFechaKMP(
    onFechaSeleccionada: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    // 1. Creamos el estado del picker
    val datePickerState = rememberDatePickerState()

    // 2. Usamos el Dialog estándar
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onFechaSeleccionada(datePickerState.selectedDateMillis)
                }
            ) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        // El contenido del diálogo
        DatePicker(state = datePickerState)
    }
}

@Composable
fun ListaCompeticiones(
    competiciones: List<Competicion>, // Cambiado a lista de objetos
    idSeleccionado: String?,
    onSeleccionar: (String) -> Unit,
    onEliminar: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)) // Redondea las esquinas
            .border(
                border = BorderStroke(1.dp, Color.Gray), // Borde (puedes usar otro color si quieres)
                shape = RoundedCornerShape(10.dp),

                )
            .padding(16.dp), // Padding interno para que el contenido no pegue al borde
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (competiciones.isEmpty()) {
            // Este Box se encarga de centrar el contenido en el espacio restante
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "No tienes competiciones creadas",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            // USAMOS Column en lugar de LazyColumn para evitar el crash
            competiciones.forEach { comp ->
                val esActiva = comp.id == idSeleccionado

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeleccionar(comp.id) },
                    shape = RoundedCornerShape(12.dp),
                    // Si está seleccionada, sube la elevación y cambia el color del borde
                    elevation = CardDefaults.cardElevation(if (esActiva) 8.dp else 2.dp),
                    border = BorderStroke(
                        width = if (esActiva) 2.dp else 1.dp,
                        color = if (esActiva) Color(0xFF2196F3) else Color.LightGray
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icono lateral (azul si está activa)
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = if (esActiva) Color(0xFF2196F3) else Color.Gray
                        )

                        Spacer(Modifier.width(16.dp))

                        // Datos centrales
                        Column(modifier = Modifier.weight(1f)) {
                            Text(comp.nombre, fontWeight = FontWeight.Bold)
                            Text("Entidad: ${comp.entidad}", style = MaterialTheme.typography.bodySmall)
                            Text("Fecha: ${comp.fecha}", style = MaterialTheme.typography.bodySmall)
                            Text("Lugar: ${comp.lugar}", style = MaterialTheme.typography.bodySmall)
                        }

                        // Botones de acción
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (esActiva) {
                                Text("Activa", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
                            } else {
                                Text("Activar", color = Color.Gray)
                            }

                            IconButton(onClick = { onEliminar(comp.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}