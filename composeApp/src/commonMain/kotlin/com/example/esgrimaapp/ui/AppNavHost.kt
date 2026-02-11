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
import com.example.esgrimaapp.ui.login.RegisterScreen


@Composable
fun AppNavHost() {
    val rootNavController = rememberNavController()

    NavHost(navController = rootNavController, startDestination = "login") {
        // Pantallas sin Scaffold
        composable("login") {
            LoginScreen(rootNavController)
        }

        // La "Pantalla Principal" que contiene el Scaffold, Drawer y el resto de la App
        composable("homePage") {
            MainScaffold()
        }
    }
}