package com.example.esgrimaapp.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel

class LoginScreenModel : ScreenModel {
    // Estados para los campos
    var usuarioState by mutableStateOf("")
    var passwordState by mutableStateOf("")
    var errorState by mutableStateOf<String?>(null)

    // Credenciales hardcoded
    private val USER_VALIDO = "admin"
    private val PASS_VALIDA = "1234"

    fun intentarLogin(onSuccess: () -> Unit) {
        if (usuarioState == USER_VALIDO && passwordState == PASS_VALIDA) {
            errorState = null
            onSuccess() // Esto navegará al MainScaffold
        } else {
            errorState = "Usuario o contraseña incorrectos"
        }
    }
}