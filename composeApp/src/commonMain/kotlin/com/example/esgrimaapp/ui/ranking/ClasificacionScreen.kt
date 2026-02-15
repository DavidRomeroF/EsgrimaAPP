package com.example.esgrimaapp.ui.ranking

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.esgrimaapp.data.EstadisticasTirador
import kotlinx.datetime.format.Padding

class RankingScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = rememberScreenModel { RankingViewModel() }
        val ranking by viewModel.ranking.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFFF8FAFC) // Fondo gris muy claro profesional
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
            ){
                PantallaRanking(ranking)
            }
        }
    }
}

@Composable
fun PantallaRanking(ranking: List<EstadisticasTirador>){
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Clasificación",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ranking de tiradores basado en victorias y diferencia de tocados",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        if (ranking.isEmpty()) {
            // Estado vacío (image_00f308.png)
            PantallaSinResultados()
        } else {
            // Tabla de ranking (image_00f385.png)
            TablaRanking(ranking)
            Spacer(Modifier.height(16.dp))
            LeyendaRanking()
        }
    }
}

@Composable
fun TablaRanking(ranking: List<EstadisticasTirador>) {
    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column {
            // Cabecera de la tabla
            Row(
                Modifier.fillMaxWidth().background(Color(0xFFF8FAFC)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pos.", Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
                Text("Tirador", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                Text("V", Modifier.weight(0.4f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Text("D", Modifier.weight(0.4f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Text("TD", Modifier.weight(0.5f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Text("TR", Modifier.weight(0.5f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Text("Dif", Modifier.weight(0.5f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Text("Índice", Modifier.weight(0.8f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
            }

            Divider(color = Color(0xFFE2E8F0))

            // Filas
            ranking.forEachIndexed { index, stats ->
                val esPodio = index < 3
                val colorFila = if (esPodio) Color(0xFFFFFBEB) else Color.Transparent

                Row(
                    Modifier.fillMaxWidth().background(colorFila).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${index + 1} ${if(index==0) "🏆" else ""}", Modifier.weight(0.5f))
                    Text(stats.tirador.nombre, Modifier.weight(1.5f), fontWeight = FontWeight.Medium)

                    // Victorias en verde suave como en tu captura
                    BadgeVictoria(stats.victorias.toString(), Modifier.weight(0.4f))
                    BadgeDerrota(stats.derrotas.toString(), Modifier.weight(0.4f))

                    Text(stats.tocadosDados.toString(), Modifier.weight(0.5f), textAlign = TextAlign.Center)
                    Text(stats.tocadosRecibidos.toString(), Modifier.weight(0.5f), textAlign = TextAlign.Center)
                    Text(stats.diferencia.toString(), Modifier.weight(0.5f), textAlign = TextAlign.Center)

                    Text("${stats.indice}", Modifier.weight(0.8f), textAlign = TextAlign.End,
                        color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                }
                Divider(color = Color(0xFFF1F5F9))
            }
        }
    }
}

@Composable
fun BadgeVictoria(valor: String, modifier: Modifier) {
    Surface(
        modifier = modifier.padding(horizontal = 4.dp),
        color = Color(0xFFDCFCE7),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(valor, color = Color(0xFF166534), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 2.dp))
    }
}

@Composable
fun BadgeDerrota(valor: String, modifier: Modifier) {
    Surface(
        modifier = modifier.padding(horizontal = 4.dp),
        color = Color(0xFFFEE2E2),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(valor, color = Color(0xFF991B1B), textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 2.dp))
    }
}

@Composable
fun LeyendaRanking() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9).copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF2563EB)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Interpretación de Datos",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Distribución en 2 columnas para que no ocupe tanto espacio vertical
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ItemLeyenda("V", "Victorias totales", Color(0xFF166534))
                    ItemLeyenda("D", "Derrotas totales", Color(0xFF991B1B))
                    ItemLeyenda("TD", "Tocados Dados (puntos a favor)", Color(0xFF475569))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ItemLeyenda("TR", "Tocados Recibidos (puntos en contra)", Color(0xFF475569))
                    ItemLeyenda("Dif", "Diferencia neta (TD - TR)", Color(0xFF2563EB))
                    ItemLeyenda("%", "Efectividad de victoria", Color(0xFF475569))
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

            // Explicación del Índice (Lo más importante para el ranking)
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Índice:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "(V × 1000) + Diferencia. Determina la posición oficial.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1E40AF)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemLeyenda(sigla: String, descripcion: String, colorSigla: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$sigla:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = colorSigla,
            modifier = Modifier.width(28.dp)
        )
        Text(
            text = descripcion,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
fun PantallaSinResultados() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono de trofeo/copa estilizado
            Icon(
                imageVector = Icons.Default.EmojiEvents, // O Icons.Default.EmojiEvents si usas Material Extended
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF94A3B8) // Gris azulado suave
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No hay resultados todavía",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Los resultados aparecerán cuando se completen los asaltos",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color(0xFF64748B)
            )
        }
    }
}