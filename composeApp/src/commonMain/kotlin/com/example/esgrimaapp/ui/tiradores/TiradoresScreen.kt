package com.example.esgrimaapp.ui.tiradores

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.esgrimaapp.ui.competicion.CompeticionLayout
import com.example.esgrimaapp.ui.competicion.CompeticionUIState
import com.example.esgrimaapp.ui.competicion.CompeticionViewModel

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
){

    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // Importante para que ocupe toda la pantalla
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Añade espacio entre bloques automáticamente
    ) {
        // 1. Encabezado
        item {
            Column {
                Text(
                    text = "Tiradores de la Competición",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}