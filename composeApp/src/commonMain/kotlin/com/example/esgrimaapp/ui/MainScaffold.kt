package com.example.esgrimaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aprendepalabras.ui.theme.Fondo
import com.example.esgrimaapp.ui.competicion.CompeticionScreen
import com.example.esgrimaapp.ui.home.DashboardScreen
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.esgrimaapp.ui.Register.RegisterScreen
import com.example.esgrimaapp.ui.arbitros.ArbitrosScreen
import com.example.esgrimaapp.ui.asaltosGrupos.ResultadosScreen
import com.example.esgrimaapp.ui.clasificacion.ClasificacionScreen
import com.example.esgrimaapp.ui.login.LoginScreen
import com.example.esgrimaapp.ui.poules.PoulesLayout
import com.example.esgrimaapp.ui.poules.PoulesScreen
import com.example.esgrimaapp.ui.ranking.RankingScreen
import com.example.esgrimaapp.ui.tiradores.TiradoresScreen
import com.example.esgrimaapp.ui.usuarios.UsuariosScreen
import esgrimaapp.composeapp.generated.resources.database


class MainScaffoldScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        // 1. Navegador de nivel raíz (el que está en App.kt)
        // Se usa para cerrar sesión y volver al Login
        val rootNavigator = LocalNavigator.currentOrThrow

        // 2. Navegador interno para las secciones (Dashboard, Usuarios, etc.)
        Navigator(DashboardScreen()) { sectionNavigator ->
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                        // Cabecera fija
                        DrawerHeader()
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                        // CUERPO CON SCROLL VERTICAL
                        Column(
                            modifier = Modifier
                                .weight(1f) // Empuja el botón de cerrar sesión al final
                                .verticalScroll(rememberScrollState())
                        ) {
                            NavMenuLateral(
                                icono = Res.drawable.grid,
                                titulo = "Pantalla principal",
                                onClick = {
                                    sectionNavigator.replaceAll(DashboardScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavMenuLateral(
                                icono = Res.drawable.database,
                                titulo = "Usuarios",
                                onClick = {
                                    sectionNavigator.replaceAll(UsuariosScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavMenuLateral(
                                icono = Res.drawable.swords,
                                titulo = "Competición",
                                onClick = {
                                    sectionNavigator.replaceAll(CompeticionScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavMenuLateral(
                                icono = Res.drawable.groups,
                                titulo = "Tiradores",
                                onClick = {
                                    sectionNavigator.replaceAll(TiradoresScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavMenuLateral(
                                icono = Res.drawable.personAdd,
                                titulo = "Árbitros",
                                onClick = {
                                    sectionNavigator.replaceAll(ArbitrosScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavMenuLateral(
                                icono = Res.drawable.grid, // Cambia si tienes otro icono para Poules
                                titulo = "Grupos (Poules)",
                                onClick = {
                                    sectionNavigator.replaceAll(PoulesScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavMenuLateral(
                                icono = Res.drawable.assignment,
                                titulo = "Resultados",
                                onClick = {
                                    sectionNavigator.replaceAll(ResultadosScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavMenuLateral(
                                icono = Res.drawable.flowchart,
                                titulo = "Tablón (Eliminatorias)",
                                onClick = {
                                    sectionNavigator.replaceAll(ClasificacionScreen())
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }

                        // SECCIÓN DE CIERRE DE SESIÓN (Fija al fondo)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    tint = Color.Red
                                )
                            },
                            label = { Text("Cerrar Sesión", color = Color.Red, fontWeight = FontWeight.Bold) },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    // IMPORTANTE: Al usar rootNavigator, volvemos a la raíz
                                    // Esto DESTRUYE el Scaffold y quita el TopAppBar
                                    rootNavigator.replaceAll(LoginScreen())
                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            ) {
                // EL SCAFFOLD ESTÁ AQUÍ DENTRO
                // Solo existe mientras estemos logueados
                Scaffold(
                    containerColor = Fondo,
                    topBar = {
                        TopAppBar(
                            title = { Text("Esgrima Manager") },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Abrir Menú")
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White
                            )
                        )
                    }
                ) { padding ->
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        // CurrentScreen muestra el contenido del sectionNavigator
                        CurrentScreen()
                    }
                }
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
                tint = androidx.compose.ui.graphics.Color.Blue
            )
        },
        label = { Text(titulo) },
        selected = false,
        onClick = onClick
    )
}

@Composable
fun DrawerHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.logo_app),
            contentDescription = null,
            modifier = Modifier.size(60.dp).padding(10.dp)
        )
        Column {
            Text("Esgrima Manager", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Admin: Juan Pérez", fontSize = 14.sp) // Aquí usarás los datos de tu tabla de auth
        }
    }
}

