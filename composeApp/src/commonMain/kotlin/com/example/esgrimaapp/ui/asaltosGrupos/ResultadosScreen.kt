package com.example.esgrimaapp.ui.asaltosGrupos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.aprendepalabras.ui.theme.Fondo
import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadoAsalto

class ResultadosScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { ResultadosViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Fondo // Usando tu variable de color
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                ResultadosLayout(uiState, viewModel)
            }
        }
    }
}

@Composable
fun ResultadosLayout(uiState: ResultadosUIState, viewModel: ResultadosViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Gestión de Resultados",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Introduce los resultados de los asaltos",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        Spacer(Modifier.height(24.dp))

        // Selector de Pestañas (Tabs)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotonTab(
                texto = "Fase de Grupos",
                activo = uiState.faseGruposActiva,
                onClick = { viewModel.setFase(true) }
            )
        }

        Spacer(Modifier.height(32.dp))

        // Lógica de visualización según la pestaña y el estado de los datos
        when {
            uiState.faseGruposActiva && !uiState.hayGrupos -> {
                MensajeVacioCentral(
                    mensaje = "No hay grupos creados todavía",
                    subtexto = "Crea los grupos en la sección \"Grupos (Poules)\""
                )
            }
            !uiState.faseGruposActiva -> {
                MensajeVacioCentral(
                    mensaje = "No hay tabla eliminatoria creada todavía",
                    subtexto = "Genera la tabla eliminatoria en la sección \"Tablón (Eliminatorias)\""
                )
            }
            else -> {
                ListaAsaltosPorGrupo(uiState, viewModel)
            }
        }
    }

    // Diálogo emergente para anotar puntos
    uiState.asaltoParaEditar?.let { asalto ->
        DialogoPuntos(
            asalto = asalto,
            onDismiss = { viewModel.abrirEditor(null) },
            onSave = { pA, pB, estado -> viewModel.registrarPuntos(pA, pB, estado) }
        )
    }
}

@Composable
fun ListaAsaltosPorGrupo(uiState: ResultadosUIState, viewModel: ResultadosViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        uiState.asaltosPorGrupo.forEach { (grupoId, asaltos) ->
            item {
                Column {
                    Text(text = "Grupo $grupoId", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(text = "Pista ${asaltos.firstOrNull()?.pista ?: "?"} • Primera a 5 tocados",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            items(asaltos) { asalto ->
                TarjetaAsaltoIndividual(asalto, onEdit = { viewModel.abrirEditor(asalto) })
            }
        }
    }
}

@Composable
fun TarjetaAsaltoIndividual(asalto: Asalto, onEdit: () -> Unit) {
    // 1. Determinar el color base y texto de la etiqueta (Badge)
    val (colorEstado, textoEstado) = when (asalto.estado) {
        EstadoAsalto.FINALIZADO -> Color(0xFF10B981) to "TERMINADO"
        EstadoAsalto.INCOMPARECENCIA_A,
        EstadoAsalto.INCOMPARECENCIA_B,
        EstadoAsalto.INCOMPARECENCIA_AMBOS -> Color(0xFFEF4444) to "ABSENCE"
        else -> Color(0xFF64748B) to asalto.horaEstimada
    }

    // 2. Color dinámico para los nombres de los tiradores
    val colorNombreA = if (asalto.estado == EstadoAsalto.INCOMPARECENCIA_A ||
        asalto.estado == EstadoAsalto.INCOMPARECENCIA_AMBOS) Color.Red else Color.Unspecified
    val colorNombreB = if (asalto.estado == EstadoAsalto.INCOMPARECENCIA_B ||
        asalto.estado == EstadoAsalto.INCOMPARECENCIA_AMBOS) Color.Red else Color.Unspecified

    // 3. Color del borde: Rojo si hay ausencia, Verde si terminó, Gris si está pendiente
    val colorBorde = when (asalto.estado) {
        EstadoAsalto.PROGRAMADO -> Color(0xFFE2E8F0)
        EstadoAsalto.FINALIZADO -> Color(0xFF10B981).copy(alpha = 0.3f)
        else -> Color(0xFFEF4444).copy(alpha = 0.3f) // Para cualquier ABSENCE
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, colorBorde)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera: Etiqueta y Pista
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = colorEstado.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = textoEstado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = colorEstado,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(text = "Pista ${asalto.pista}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            Spacer(Modifier.height(12.dp))

            // Cuerpo: Nombres y Marcador
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = asalto.tiradorA.nombre,
                    color = colorNombreA,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${asalto.tocadosA}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if(asalto.estado == EstadoAsalto.FINALIZADO && asalto.tocadosA > asalto.tocadosB) Color(0xFF2563EB) else Color.Unspecified
                    )
                    Text("-", modifier = Modifier.padding(horizontal = 8.dp), color = Color.LightGray)
                    Text(
                        text = "${asalto.tocadosB}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if(asalto.estado == EstadoAsalto.FINALIZADO && asalto.tocadosB > asalto.tocadosA) Color(0xFF2563EB) else Color.Unspecified
                    )
                }

                Text(
                    text = asalto.tiradorB.nombre,
                    color = colorNombreB,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                }
            }

            // --- NUEVO SECCIÓN: ÁRBITRO ---
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF1F5F9),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.PersonSearch, // O Icons.Default.Gavel si prefieres
                    contentDescription = "Árbitro",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Árbitro: ${asalto.arbitro?.nombre ?: "Sin asignar"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (asalto.arbitro != null) Color(0xFF64748B) else Color(0xFF94A3B8),
                    fontStyle = if (asalto.arbitro == null) FontStyle.Italic else FontStyle.Normal
                )
            }
        }
    }
}

@Composable
fun MensajeVacioCentral(mensaje: String, subtexto: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.DeveloperBoard, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
            Spacer(Modifier.height(16.dp))
            Text(text = mensaje, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
            Text(text = subtexto, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun BotonTab(texto: String, activo: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (activo) Color(0xFF2563EB) else Color.White,
            contentColor = if (activo) Color.White else Color(0xFF64748B)
        ),
        border = if (!activo) BorderStroke(1.dp, Color(0xFFE2E8F0)) else null,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(texto)
    }
}

@Composable
fun DialogoPuntos(
    asalto: Asalto,
    onDismiss: () -> Unit,
    onSave: (puntosA: Int, puntosB: Int, estado: EstadoAsalto) -> Unit
) {
    var puntosA by remember { mutableStateOf(asalto.tocadosA) }
    var puntosB by remember { mutableStateOf(asalto.tocadosB) }
    var estadoLocal by remember { mutableStateOf(asalto.estado) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        title = {
            Text("Anotar Resultado", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pista ${asalto.pista} • Límite 5 tocados", color = Color.Gray, style = MaterialTheme.typography.bodySmall)

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ColumnaContador(asalto.tiradorA.nombre, puntosA) { puntosA = it }
                    Text("vs", fontWeight = FontWeight.Bold, color = Color.LightGray)
                    ColumnaContador(asalto.tiradorB.nombre, puntosB) { puntosB = it }
                }

                Spacer(Modifier.height(24.dp))
                Divider(color = Color(0xFFF1F5F9))
                Spacer(Modifier.height(16.dp))

                Text("Estados Especiales", style = MaterialTheme.typography.labelLarge, color = Color(0xFF64748B))

                // Botón para LIMPIAR incomparecencias y volver a PROGRAMADO
                TextButton(
                    onClick = {
                        estadoLocal = EstadoAsalto.PROGRAMADO
                        puntosA = 0
                        puntosB = 0
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Reiniciar a modo Programado", style = MaterialTheme.typography.labelMedium)
                }

                Spacer(Modifier.height(8.dp))

                // Fila de botones de incomparecencia con lógica Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val modA = Modifier.weight(1f)

                    // ABSENCE A
                    BotonEstadoEspecial(
                        texto = "ABS. A",
                        seleccionado = estadoLocal == EstadoAsalto.INCOMPARECENCIA_A,
                        modifier = modA
                    ) {
                        if (estadoLocal == EstadoAsalto.INCOMPARECENCIA_A) estadoLocal = EstadoAsalto.PROGRAMADO
                        else { estadoLocal = EstadoAsalto.INCOMPARECENCIA_A; puntosA = 0; puntosB = 5 }
                    }

                    // ABSENCE B
                    BotonEstadoEspecial(
                        texto = "ABS. B",
                        seleccionado = estadoLocal == EstadoAsalto.INCOMPARECENCIA_B,
                        modifier = modA
                    ) {
                        if (estadoLocal == EstadoAsalto.INCOMPARECENCIA_B) estadoLocal = EstadoAsalto.PROGRAMADO
                        else { estadoLocal = EstadoAsalto.INCOMPARECENCIA_B; puntosA = 5; puntosB = 0 }
                    }

                    // ABSENCE TWO
                    BotonEstadoEspecial(
                        texto = "BOTH",
                        seleccionado = estadoLocal == EstadoAsalto.INCOMPARECENCIA_AMBOS,
                        modifier = modA
                    ) {
                        if (estadoLocal == EstadoAsalto.INCOMPARECENCIA_AMBOS) estadoLocal = EstadoAsalto.PROGRAMADO
                        else { estadoLocal = EstadoAsalto.INCOMPARECENCIA_AMBOS; puntosA = 0; puntosB = 0 }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Si no hay incomparecencia pero hay puntos, es FINALIZADO.
                    // Si no hay nada, sigue PROGRAMADO.
                    val finalEstado = when {
                        estadoLocal != EstadoAsalto.PROGRAMADO -> estadoLocal
                        puntosA > 0 || puntosB > 0 -> EstadoAsalto.FINALIZADO
                        else -> EstadoAsalto.PROGRAMADO
                    }
                    onSave(puntosA, puntosB, finalEstado)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun BotonEstadoEspecial(texto: String, seleccionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (seleccionado) Color(0xFFFEF2F2) else Color.Transparent,
            contentColor = if (seleccionado) Color.Red else Color(0xFF64748B)
        ),
        border = BorderStroke(1.dp, if (seleccionado) Color.Red else Color(0xFFE2E8F0))
    ) {
        Text(texto, style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@Composable
fun ColumnaContador(nombre: String, puntos: Int, onPuntosChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(nombre, fontWeight = FontWeight.Medium, maxLines = 1)
        Spacer(Modifier.height(8.dp))

        IconButton(
            onClick = { onPuntosChange(puntos + 1) },
            enabled = puntos < 5,
            modifier = Modifier.background(Color(0xFFF1F5F9), CircleShape)
        ) {
            Icon(Icons.Default.Add, null)
        }

        Text(
            text = puntos.toString(),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        IconButton(
            onClick = { onPuntosChange(puntos - 1) },
            enabled = puntos > 0,
            modifier = Modifier.background(Color(0xFFF1F5F9), CircleShape)
        ) {
            Icon(Icons.Default.Remove, null)
        }
    }
}