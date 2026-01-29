package com.example.esgrimaapp.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.esgrimaapp.ui.login.LoginScreen
import com.example.esgrimaapp.ui.login.LoginViewModel
import com.example.esgrimaapp.ui.login.RegisterScreen


@Composable
fun AppNavHost(
//    preferencesViewModel: PreferencesViewModel,
    navController: NavHostController = rememberNavController()
) {
    // En KMP, el viewModel() funciona igual gracias a la librería de androidx.lifecycle
//    val loginViewModel: LoginViewModel = viewModel { LoginViewModel() }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen()
        }

//        composable("main") {
//            // Asumo que MainScaffold también lo has movido a commonMain
//            MainScaffold(navController) { VentanaMain(navController) }
//        }

    }
}