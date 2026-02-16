package com.example.esgrimaapp.ui.clasificacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.example.aprendepalabras.ui.theme.Fondo
import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.data.Usuario
import com.example.esgrimaapp.ui.ranking.PantallaRanking
import com.example.esgrimaapp.ui.ranking.PantallaSinResultados
import com.example.esgrimaapp.ui.ranking.RankingViewModel
import com.example.esgrimaapp.ui.ranking.TablaRanking

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.math.pow

class ClasificacionScreen : Screen {
    @Composable
    override fun Content() {
        val rankingViewModel = rememberScreenModel { RankingViewModel() }
        val clasificacionViewModel = rememberScreenModel { ClasificacionViewModel() }

        val ranking by rankingViewModel.ranking.collectAsState()
        val uiState by clasificacionViewModel.uiState.collectAsState()

        var tabSeleccionada by remember { mutableIntStateOf(0) }

        Scaffold(
            containerColor = Color(0xFFF8FAFC)
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                HeaderClasificacion(tabSeleccionada) { tabSeleccionada = it }

                AnimatedContent(targetState = tabSeleccionada, label = "tabs") { targetTab ->
                    when (targetTab) {
                        0 -> PantallaRanking(ranking)
                        1 -> VistaTablon(uiState, clasificacionViewModel)
                    }
                }
            }
        }
    }
}

// --- CABECERA ---
@Composable
fun HeaderClasificacion(seleccionado: Int, onTabSelected: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(top = 16.dp)) {
        Text(
            text = "Resultados y Clasificación",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        TabRow(
            selectedTabIndex = seleccionado,
            containerColor = Color.White,
            contentColor = Color(0xFF2563EB)
        ) {
            Tab(selected = seleccionado == 0, onClick = { onTabSelected(0) }, text = { Text("Ranking Poules") })
            Tab(selected = seleccionado == 1, onClick = { onTabSelected(1) }, text = { Text("Tablón Final") })
        }
    }
}

// --- VISTA TABLÓN ---
@Composable
fun VistaTablon(state: ClasifiacionUIState, viewModel: ClasificacionViewModel) {
    if (state.asaltosExistentes.isEmpty()) {
        ConfiguracionInicial(state, viewModel)
    } else {
        VisualizadorBracketProfesional(state, viewModel)
    }
}

// --- VISUALIZADOR PROFESIONAL CON ZOOM Y ALINEACIÓN ---
@Composable
fun VisualizadorBracketProfesional(state: ClasifiacionUIState, viewModel: ClasificacionViewModel) {
    var asaltoParaEditar by remember { mutableStateOf<Asalto?>(null) }

    // Estado para Zoom y Pan (Adaptabilidad Total para móvil)
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Diálogo de puntos
    asaltoParaEditar?.let { asalto ->
        DialogoPuntos(
            asalto = asalto,
            onDismiss = { asaltoParaEditar = null },
            onSave = { pA, pB, estado ->
                viewModel.registrarResultado(asalto, pA, pB)
                asaltoParaEditar = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 2f)
                    offset += pan
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .padding(60.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val agrupados = state.asaltosExistentes.groupBy { it.id.split("_")[1].toInt() }
                val nivelesOrdenados = agrupados.keys.sortedByDescending { it }

                nivelesOrdenados.forEachIndexed { index, nivel ->
                    val asaltosDeRonda = agrupados[nivel] ?: emptyList()
                    val factorEspaciado = 2.0.pow(index.toDouble()).toFloat()
                    val alturaBloque = 120.dp * factorEspaciado

                    Column(
                        modifier = Modifier.width(260.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = if (nivel == 2) "FINAL" else "TABLA DE $nivel",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF475569),
                            letterSpacing = 2.sp
                        )

                        Spacer(Modifier.height(32.dp))

                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            val asaltosOrdenados = asaltosDeRonda.sortedBy { it.id.split("_").last().toInt() }

                            asaltosOrdenados.forEach { asalto ->
                                val clicable = asalto.tiradorA.nombre != "---" && asalto.tiradorB.nombre != "---"

                                Box(
                                    modifier = Modifier
                                        .height(alturaBloque)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TarjetaCruceProfesional(asalto, clicable) {
                                        asaltoParaEditar = asalto
                                    }
                                }
                            }
                        }
                    }

                    // Separador visual entre columnas (Flechas)
                    if (index < nivelesOrdenados.size - 1) {
                        Spacer(Modifier.width(20.dp))
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFFCBD5E1),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(20.dp))
                    }
                }

                // --- NUEVO: TARJETA DE CAMPEÓN FINAL ---
                val finalAsalto = agrupados[2]?.firstOrNull()
                if (finalAsalto != null && finalAsalto.estado == EstadoAsalto.FINALIZADO) {
                    val campeon = if (finalAsalto.tocadosA > finalAsalto.tocadosB) finalAsalto.tiradorA else finalAsalto.tiradorB

                    Spacer(Modifier.width(60.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trofeo",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "CAMPEÓN",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 8.dp,
                            modifier = Modifier.width(220.dp)
                        ) {
                            Text(
                                text = campeon.nombre,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaCruceProfesional(asalto: Asalto, clicable: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = clicable,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp) // Aumentamos un poco la altura para comodidad
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, if(clicable) Color(0xFFE2E8F0) else Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val esPaseDirecto = asalto.estado == EstadoAsalto.FINALIZADO &&
                    (asalto.tiradorA.nombre == "---" || asalto.tiradorB.nombre == "---")

            // Usamos Box con weight para asegurar que el espacio sea 50/50 exacto
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FilaTiradorProfesional(
                    nombre = asalto.tiradorA.nombre,
                    puntos = asalto.tocadosA,
                    esGanador = asalto.estado == EstadoAsalto.FINALIZADO && asalto.tocadosA > asalto.tocadosB,
                    esPaseDirecto = esPaseDirecto
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FilaTiradorProfesional(
                    nombre = asalto.tiradorB.nombre,
                    puntos = asalto.tocadosB,
                    esGanador = asalto.estado == EstadoAsalto.FINALIZADO && asalto.tocadosB > asalto.tocadosA,
                    esPaseDirecto = esPaseDirecto
                )
            }
        }
    }
}

@Composable
fun FilaTiradorProfesional(nombre: String, puntos: Int, esGanador: Boolean,esPaseDirecto: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (nombre == "---") "EXENTO" else nombre,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (esGanador) FontWeight.Bold else FontWeight.Normal,
            color = if (nombre == "---") Color.LightGray else if (esGanador) Color(0xFF1E293B) else Color(0xFF64748B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (nombre != "---") {
            Surface(
                color = if (esGanador) Color(0xFFDBEAFE) else Color(0xFFF8FAFC),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    // Lógica solicitada: Si es pase directo ponemos "BYE", si no, los puntos
                    text = if (esPaseDirecto) "BYE" else puntos.toString(),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = if (esGanador) Color(0xFF2563EB) else Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun ConfiguracionInicial(state: ClasifiacionUIState, viewModel: ClasificacionViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.AccountTree, null, modifier = Modifier.size(64.dp), tint = Color(0xFF94A3B8))
        Text("Configurar Fase Final", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(8, 16, 32).forEach { tam ->
                OpcionTamano(tam, state.tamanoSeleccionado == tam) { viewModel.seleccionarTamano(tam) }
            }
        }
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = { viewModel.generarTablon() },
            enabled = state.faseGruposTerminada && state.tamanoSeleccionado != 0,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
        ) { Text("Generar Cuadro") }
    }
}

@Composable
fun DialogoPuntos(
    asalto: Asalto,
    onDismiss: () -> Unit,
    onSave: (puntosA: Int, puntosB: Int, estado: EstadoAsalto) -> Unit
) {
    var puntosA by remember { mutableIntStateOf(asalto.tocadosA) }
    var puntosB by remember { mutableIntStateOf(asalto.tocadosB) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        title = { Text("Anotar Resultado", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Límite 15 tocados • No empates", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    ContadorPuntosMini(asalto.tiradorA.nombre, puntosA, Color(0xFF2563EB)) { puntosA = it }
                    Text("vs", fontWeight = FontWeight.Bold, color = Color.LightGray)
                    ContadorPuntosMini(asalto.tiradorB.nombre, puntosB, Color(0xFFEF4444)) { puntosB = it }
                }
                if (puntosA == puntosB && puntosA != 0) {
                    Text("El empate no es válido", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(puntosA, puntosB, EstadoAsalto.FINALIZADO) },
                enabled = puntosA != puntosB,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun ContadorPuntosMini(nombre: String, puntos: Int, color: Color, onValue: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(nombre.take(10), maxLines = 1, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        IconButton(onClick = { if (puntos < 15) onValue(puntos + 1) }) { Icon(Icons.Default.KeyboardArrowUp, null, tint = color) }
        Text("$puntos", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = color)
        IconButton(onClick = { if (puntos > 0) onValue(puntos - 1) }) { Icon(Icons.Default.KeyboardArrowDown, null) }
    }
}

@Composable
fun OpcionTamano(tamano: Int, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.width(90.dp).height(60.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (seleccionado) Color(0xFF2563EB) else Color.White,
        border = BorderStroke(2.dp, if (seleccionado) Color(0xFF2563EB) else Color(0xFFE2E8F0))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("T$tamano", fontWeight = FontWeight.Bold, color = if (seleccionado) Color.White else Color.Black)
        }
    }
}