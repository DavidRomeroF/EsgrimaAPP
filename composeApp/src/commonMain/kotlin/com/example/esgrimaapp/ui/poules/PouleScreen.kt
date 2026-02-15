package com.example.esgrimaapp.ui.poules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.aprendepalabras.ui.theme.Fondo
import com.example.esgrimaapp.data.Poule

class PoulesScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { PoulesViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Fondo
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                PoulesLayout(uiState, viewModel)
            }
        }
    }
}
@Composable
fun PoulesLayout(uiState: PoulesUIState, viewModel: PoulesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Título de la sección
        Text(
            text = "Grupos (Poules)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        // 1. CASO: No hay competición activa
        if (!uiState.hayCompeticionActiva) {
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // Naranja muy claro
                border = BorderStroke(1.dp, Color(0xFFFEF3C7))
            ) {
                Text(
                    text = "No hay ninguna competición activa. Crea una competición antes de gestionar los grupos.",
                    modifier = Modifier.padding(24.dp),
                    color = Color(0xFF92400E)
                )
            }
            return@Column
        }

        Text(
            text = "Organiza los tiradores en grupos para la fase de clasificación",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF64748B)
        )

        Spacer(Modifier.height(32.dp))

        // 2. CASO: Hay competición pero no hay grupos creados
        if (uiState.gruposGenerados.isEmpty()) {
            ConfiguracionGruposCard(uiState, viewModel)
        } else {
            // 3. CASO: Grupos ya listos
            ListaGruposVista(uiState, viewModel)
        }
    }
}

@Composable
fun ConfiguracionGruposCard(uiState: PoulesUIState, viewModel: PoulesViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Crear Grupos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            Text(
                "Tiradores disponibles: ${uiState.tiradoresInscritos.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Árbitros disponibles: ${uiState.arbitrosInscritos.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = uiState.cantidadGrupos,
                    onValueChange = { viewModel.onGruposChange(it) },
                    label = { Text("Número de Grupos") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = uiState.cantidadPistas,
                    onValueChange = { viewModel.onPistasChange(it) },
                    label = { Text("Número de Pistas") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Muestra info de reparto (Ej: 2 grupos de 3)
            if (uiState.infoReparto.isNotEmpty()) {
                Text(
                    text = uiState.infoReparto,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFF2563EB),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.generarGrupos() },
                enabled = uiState.puedeGenerar,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Icon(Icons.Default.Shuffle, null)
                Spacer(Modifier.width(8.dp))
                Text("Generar Grupos Automáticamente")
            }
        }
    }
}

@Composable
fun ListaGruposVista(uiState: PoulesUIState, viewModel: PoulesViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${uiState.gruposGenerados.size} grupos creados con ${uiState.tiradoresInscritos.size} tiradores",
            color = Color(0xFF64748B)
        )
        TextButton(onClick = { viewModel.reiniciarGrupos() }) {
            Text("Reiniciar Grupos", color = Color.Red)
        }
    }

    Spacer(Modifier.height(16.dp))

    // Grid de Grupos
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 350.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(uiState.gruposGenerados) { poule ->
            PouleCard(poule)
        }
    }
}

@Composable
fun PouleCard(poule: Poule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp)
                        .background(Color(0xFFF5F3FF), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.GridView, null, tint = Color(0xFF8B5CF6))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(poule.nombre, fontWeight = FontWeight.Bold)
                    Text(
                        "Pista ${poule.pista} • Árbitro: ${poule.arbitroAsignado?.nombre ?: "Sin asignar"}",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Lista de tiradores en el grupo
            poule.tiradores.forEach { tirador ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(4.dp)).padding(8.dp)
                ) {
                    Text(tirador.nombre, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = 0f, // Aquí irá el progreso de asaltos luego
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF8B5CF6),
                trackColor = Color(0xFFE2E8F0)
            )
            Text(
                "Asaltos: 0 / ${poule.asaltosTotales}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}