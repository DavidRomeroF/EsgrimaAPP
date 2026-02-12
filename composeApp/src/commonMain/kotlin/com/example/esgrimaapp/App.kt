package com.example.esgrimaapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.example.esgrimaapp.ui.LoginScreen
@Composable
fun App() {
    MaterialTheme {
        // El Navigator de nivel raíz que empieza en el Login
        Navigator(LoginScreen()) { navigator ->
            // Puedes añadir transiciones aquí si quieres
            CurrentScreen()
        }
    }
}