package com.example.esgrimaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import esgrimaapp.composeapp.generated.resources.Res
import esgrimaapp.composeapp.generated.resources.assignment
import esgrimaapp.composeapp.generated.resources.flowchart
import esgrimaapp.composeapp.generated.resources.grid
import esgrimaapp.composeapp.generated.resources.groups
import esgrimaapp.composeapp.generated.resources.logo_app
import esgrimaapp.composeapp.generated.resources.personAdd
import esgrimaapp.composeapp.generated.resources.swords
import esgrimaapp.composeapp.generated.resources.trophy
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScaffold() {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope() // para abrir/cerrar el drawer

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(280.dp) // aquí fijas el ancho del drawer (aprox 50% pantalla móvil)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Image(
                            painter = painterResource(Res.drawable.logo_app),
                            contentDescription = "App logo",
                            modifier = Modifier
                                .size(60.dp)
                                .padding(10.dp)
                        )
                    }
                    Column {
                        Row {
                            Text("Esgrima Manager",
                                fontSize = 20.sp)
                        }
                        Row {
                            Text("NombreUsuario")
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                NavMenuLateral(
                    icono = Res.drawable.grid,
                    titulo = "Pantalla principal",
                    onClick = {/**/}
                )

                NavMenuLateral(
                    icono = Res.drawable.swords,
                    titulo = "Competición",
                    onClick = {/**/}
                )
                NavMenuLateral(
                    icono = Res.drawable.groups,
                    titulo = "Tiradores",
                    onClick = {/**/}
                )
                NavMenuLateral(
                    icono = Res.drawable.personAdd,
                    titulo = "Árbitros",
                    onClick = {/**/}
                )
                NavMenuLateral(
                    icono = Res.drawable.grid,
                    titulo = "Grupos(Poules)",
                    onClick = {/**/}
                )
                NavMenuLateral(
                    icono = Res.drawable.assignment,
                    titulo = "Resultados",
                    onClick = {/**/}
                )
                NavMenuLateral(
                    icono = Res.drawable.trophy,
                    titulo = "Clasificacíon",
                    onClick = {/**/}
                )
                NavMenuLateral(
                    icono = Res.drawable.flowchart,
                    titulo = "Tablón (Eliminatorias)",
                    onClick = {/**/}
                )



            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Mi App") },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Text("Hola esta es lo demas")
            }
        }
    }
}

@Composable
fun NavMenuLateral(
    icono: DrawableResource,
    titulo: String,
    onClick: () -> Unit
){
    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painterResource(icono),
                contentDescription = null,
                // Fuerza el color negro o azul para ver si aparece
                tint = androidx.compose.ui.graphics.Color.Black
            )
        },
        label = { Text(titulo) },
        selected = false,
        onClick = onClick
    )
}

