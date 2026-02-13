package com.example.esgrimaapp.ui.tiradores

import TiradoresViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.aprendepalabras.ui.theme.CartaTiradores
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.AppUIState
import com.example.esgrimaapp.ui.competicion.CompeticionLayout
import com.example.esgrimaapp.ui.competicion.CompeticionUIState
import com.example.esgrimaapp.ui.competicion.CompeticionViewModel
import com.example.esgrimaapp.ui.usuarios.SeccionListaUsuarios
import com.example.esgrimaapp.ui.usuarios.TagRol

class TiradoresScreen : Screen {
    @Composable
    override fun Content() {
        // En KMP con Voyager, esto es lo que evita el crash del Factory
        val viewModel = rememberScreenModel { TiradoresViewModel() }

        // Pasamos el estado recolectado para que el Layout sea "puro"
        val uiState by viewModel.uiState.collectAsState()

        // El Scaffold asegura que el fondo y el área de contenido se gestionen bien
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                TiradoresLayout(uiState, viewModel)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TiradoresLayout(
    uiState: TiradoresUIState,
    viewModel: TiradoresViewModel
) {
    // DIÁLOGO SELECTOR (Solo si hay activa, por seguridad)
    if (uiState.mostrarFormulario && uiState.hayCompeticionActiva) {
        DialogoSelectorTiradores(
            usuariosGlobales = uiState.usuariosDisponibles,
            tiradoresYaInscritos = uiState.listaTiradoresInscritos,
            onSeleccionar = { persona -> viewModel.inscribirTirador(persona) },
            onDismiss = { viewModel.toggleSelector(false) }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Encabezado Dinámico
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Participantes",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                // Mostramos el nombre real o un aviso
                Text(
                    text = if (uiState.hayCompeticionActiva)
                        "Competición: ${uiState.nombreCompeticionActiva}"
                    else "⚠️ No hay ninguna competición seleccionada",
                    color = if (uiState.hayCompeticionActiva) Color.Unspecified else Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // 2. Botón de acción (Deshabilitado si no hay activa)
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                OutlinedButton(
                    onClick = { viewModel.toggleSelector(true) },
                    enabled = uiState.hayCompeticionActiva, // <--- BLOQUEO AQUÍ
                    modifier = Modifier.width(220.dp).padding(vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if(uiState.hayCompeticionActiva) CartaTiradores else Color.LightGray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if(uiState.hayCompeticionActiva) CartaTiradores else Color.LightGray,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE2E8F0)
                    )
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Inscribir Tirador")
                }
            }
        }

        // 3. Sección de lista
        item {
            if (uiState.hayCompeticionActiva) {
                SeccionTiradoresInscritos(
                    inscritos = uiState.listaTiradoresInscritos,
                    onRemover = { id -> viewModel.removerTiradorDeCompeticion(id) }
                )
            } else {
                // Mensaje amigable si no hay nada seleccionado
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED))
                ) {
                    Text(
                        "Por favor, ve al apartado de Competiciones y selecciona una para empezar a inscribir tiradores.",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF9A3412),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SeccionTiradoresInscritos(
    inscritos: List<Usuario>,
    onRemover: (String) -> Unit, // Para desapuntar de la competición
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val esMovil = maxWidth < 750.dp
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Participantes Inscritos (${inscritos.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (esMovil) {
                // LISTA MÓVIL: Solo información y botón de remover
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    inscritos.forEach { tirador ->
                        TiradorInscritoCard(tirador, onRemover)
                    }
                }
            } else {
                // TABLA ESCRITORIO: Versión simplificada para competición
                TablaInscritos(inscritos, onRemover)
            }

            if (inscritos.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No hay tiradores inscritos todavía", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun TablaInscritos(
    inscritos: List<Usuario>,
    onRemover: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        // CABECERA SIMPLIFICADA
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Nombre", Modifier.weight(3f), fontWeight = FontWeight.Bold)
            Text("Club", Modifier.weight(2f), fontWeight = FontWeight.Bold)
            Text("Nº Fed.", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
            Text("Roles", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
            Text("", Modifier.weight(0.5f)) // Espacio para el botón X
        }

        inscritos.forEach { tirador ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tirador.nombre, Modifier.weight(3f))
                Text(tirador.club, Modifier.weight(2f), color = Color.Gray)
                Text(tirador.numeroFederacion, Modifier.weight(1.5f), color = Color.Gray)

                // Tags informativos
                Row(Modifier.weight(1.5f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (tirador.esArbitro) TagRol("Árbitro", Color(0xFFF3E8FF), Color(0xFF9333EA), Icons.Default.Shield)
                    else TagRol("Tirador", Color(0xFFDBEAFE), Color(0xFF2563EB), Icons.Default.Person)
                }

                // BOTÓN REMOVER (Desapuntar)
                IconButton(
                    onClick = { onRemover(tirador.numeroFederacion) },
                    modifier = Modifier.weight(0.5f)
                ) {
                    Icon(Icons.Default.PersonRemove, "Remover", tint = Color(0xFFEF4444))
                }
            }
            Divider(color = Color(0xFFF1F5F9))
        }
    }
}

@Composable
fun TiradorInscritoCard(
    tirador: Usuario,
    onRemover: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Nombre del tirador
                Text(
                    text = tirador.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                // Detalles secundarios
                Text(
                    text = "${tirador.club} • Fed: ${tirador.numeroFederacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags de rol para saber si además arbitra en esta competición
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagRol(
                        texto = "Tirador",
                        fondo = Color(0xFFDBEAFE),
                        contenido = Color(0xFF2563EB),
                        icono = Icons.Default.Person
                    )
                    if (tirador.esArbitro) {
                        TagRol(
                            texto = "Árbitro",
                            fondo = Color(0xFFF3E8FF),
                            contenido = Color(0xFF9333EA),
                            icono = Icons.Default.Shield
                        )
                    }
                }
            }

            // Botón de acción: Solo remover de la competición
            IconButton(
                onClick = { onRemover(tirador.numeroFederacion) },
                modifier = Modifier
                    .background(Color(0xFFFEF2F2), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonRemove,
                    contentDescription = "Desapuntar",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DialogoSelectorTiradores(
    usuariosGlobales: List<Usuario>,
    tiradoresYaInscritos: List<Usuario>,
    onSeleccionar: (Usuario) -> Unit,
    onDismiss: () -> Unit
) {
    // Filtramos para no mostrar a los que ya están en la competición
    val disponibles = usuariosGlobales.filter { global ->
        tiradoresYaInscritos.none { it.numeroFederacion == global.numeroFederacion }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Inscribir desde Base de Datos", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                if (disponibles.isEmpty()) {
                    Text(
                        "No hay usuarios disponibles. Crea nuevos usuarios en la sección de Administración.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn {
                        items(disponibles) { usuario ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSeleccionar(usuario) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B))
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(usuario.nombre, fontWeight = FontWeight.SemiBold)
                                    Text("Club: ${usuario.club}", style = MaterialTheme.typography.bodySmall)
                                }
                                Icon(Icons.Default.AddCircle, null, tint = Color(0xFF2563EB))
                            }
                            Divider(color = Color(0xFFF1F5F9))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}