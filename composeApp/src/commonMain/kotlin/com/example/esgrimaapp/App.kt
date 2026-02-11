package com.example.esgrimaapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.example.esgrimaapp.ui.LoginScreen
import com.example.esgrimaapp.ui.MainScaffold
import org.jetbrains.compose.resources.painterResource

import esgrimaapp.composeapp.generated.resources.Res
import esgrimaapp.composeapp.generated.resources.compose_multiplatform

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