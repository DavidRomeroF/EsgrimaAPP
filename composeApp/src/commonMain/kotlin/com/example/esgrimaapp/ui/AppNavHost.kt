package com.example.esgrimaapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.esgrimaapp.ui.competicion.CompeticionScreen
import com.example.esgrimaapp.ui.home.DashboardScreen
import com.example.esgrimaapp.ui.login.LoginScreen
import com.example.esgrimaapp.ui.login.LoginViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

// Pantalla de Login
class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // Al tener éxito, reemplazamos todo el stack por el Home
        LoginScreen(onLoginSuccess = {
            navigator.replaceAll(MainScreen())
        })
    }
}

// Pantalla Principal (Contenedora del Scaffold)
class MainScreen : Screen {
    @Composable
    override fun Content() {
        MainScaffold()
    }
}

// Sub-pantallas del Dashboard
class DashboardScreenContent : Screen {
    @Composable override fun Content() { DashboardScreen() }
}
