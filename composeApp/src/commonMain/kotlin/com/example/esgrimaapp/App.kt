package com.example.esgrimaapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.example.esgrimaapp.ui.FencingRepository
import com.example.esgrimaapp.ui.login.LoginScreen

@Composable
fun App() {
    MaterialTheme {
        // El Navigator raíz empieza en Login
        Navigator(LoginScreen()) { navigator ->
            // CurrentScreen() dibujará LoginScreen al principio
            CurrentScreen()
        }
    }
}