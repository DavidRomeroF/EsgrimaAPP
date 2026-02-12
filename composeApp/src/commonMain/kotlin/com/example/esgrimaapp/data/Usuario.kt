package com.example.esgrimaapp.data

data class Usuario(
    val nombre: String = "",
    val club: String = "",
    val numeroFederacion: String = "",
    val contrasenya: String = "",
    val repContrasenya: String = "",
    // Nuevos campos:
    val esArbitro: Boolean = false,
    val especialidades: List<String> = emptyList() // Usamos Set para evitar duplicados
)