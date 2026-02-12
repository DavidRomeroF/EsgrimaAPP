package com.example.esgrimaapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aprendepalabras.ui.theme.BordeCartaCompDes
import com.example.aprendepalabras.ui.theme.CartaArbitros
import com.example.aprendepalabras.ui.theme.CartaAsaltosDeGrupos
import com.example.aprendepalabras.ui.theme.CartaCompetricionesAc
import com.example.aprendepalabras.ui.theme.CartaCompetricionesDes
import com.example.aprendepalabras.ui.theme.CartaEliminatorias
import com.example.aprendepalabras.ui.theme.CartaGrupos
import com.example.aprendepalabras.ui.theme.CartaTiradores
import com.example.aprendepalabras.ui.theme.Principal
import esgrimaapp.composeapp.generated.resources.Res
import esgrimaapp.composeapp.generated.resources.swords
import org.jetbrains.compose.resources.painterResource

@Preview(showSystemUi = true)
@Composable
fun DashboardScreen() {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Títulos
        item {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text("Bienvenido, Administrador", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Panel de control del sistema de gestión de competiciones de esgrima", color = Color.Gray)
            }
        }

        // 2. Banner
        item {
            MostrarCompeticionActiva()
        }

        // 3. EL GRID DENTRO DE UNA BOX
        item {
            Box(
                modifier = Modifier
                    .heightIn(min = 100.dp, max = 1000.dp)
                    .fillMaxWidth()
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 250.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    userScrollEnabled = false
                ) {
                    item { StatCard("Tiradores", "0", Icons.Default.Person, CartaTiradores) }
                    item { StatCard("Árbitros", "0", Icons.Default.CheckCircle, CartaArbitros) }
                    item { StatCard("Grupos (Poules)", "0", Icons.Default.GridOn, CartaGrupos) }
                    item { StatCard("Asaltos de Grupo", "0/0", Icons.Default.EmojiEvents, CartaAsaltosDeGrupos) }
                    item { StatCard("Eliminatorias", "0/0", Icons.Default.Timeline, CartaEliminatorias) }
                }
            }
        }

        //Guia rapida
        item {
            GuiaRapida()
        }
    }
}

@Composable
fun StatCard(titulo: String, valor: String, icono: ImageVector, colorIcono: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color.LightGray),
        ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorIcono,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(titulo, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(valor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MostrarCompeticionActiva(){
    if(false){

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp, bottom = 30.dp),
            border = BorderStroke(1.dp, Color.LightGray),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = CartaCompetricionesAc,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.swords),
                        contentDescription = "Competición",
                        tint = Principal, // El icono queda blanco sobre el fondo azul
                        modifier = Modifier.padding(10.dp) // Espacio interno para que el icono no toque los bordes
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("NombreCompetición", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("Entidad: Federacion Valenciana", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("Fecha: 2026-02-06", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("Arma: Espada", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Text("Lugar: Madrid", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        }

    }else{
        Card(
            colors = CardDefaults.cardColors(
                containerColor = CartaCompetricionesDes
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp, bottom = 30.dp),
            border = BorderStroke(1.dp, BordeCartaCompDes),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("No hay ninguna competición activa. Crea una nueva competición para comenzar")
            }
        }
    }

}

@Composable
fun GuiaRapida(){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
            .padding(top = 20.dp, bottom = 30.dp),
        border = BorderStroke(1.dp, Color.LightGray),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio uniforme entre pasos
            ) {
                Text(
                    text = "Guía Rápida",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Paso 1
                Row {
                    Text("1 ", fontWeight = FontWeight.Bold, color = Principal)
                    Text("Crea una competición en la sección \"Competición\"", style = MaterialTheme.typography.bodyMedium)
                }

                // Paso 2
                Row {
                    Text("2 ", fontWeight = FontWeight.Bold, color = Principal)
                    Text("Añade tiradores y árbitros en sus respectivas secciones", style = MaterialTheme.typography.bodyMedium)
                }

                // Paso 3
                Row {
                    Text("3 ", fontWeight = FontWeight.Bold, color = Principal)
                    Text("Crea los grupos (poules) y asigna tiradores automáticamente", style = MaterialTheme.typography.bodyMedium)
                }

                // Paso 4
                Row {
                    Text("4 ", fontWeight = FontWeight.Bold, color = Principal)
                    Text("Introduce los resultados de cada asalto en \"Resultados\"", style = MaterialTheme.typography.bodyMedium)
                }

                // Paso 5
                Row {
                    Text("5 ", fontWeight = FontWeight.Bold, color = Principal)
                    Text("Consulta la clasificación para ver las puntuaciones", style = MaterialTheme.typography.bodyMedium)
                }

                // Paso 6
                Row {
                    Text("6 ", fontWeight = FontWeight.Bold, color = Principal)
                    Text("Genera la tabla de eliminatorias (tablón) con los mejores clasificados", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}