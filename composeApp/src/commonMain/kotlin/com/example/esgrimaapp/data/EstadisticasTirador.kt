package com.example.esgrimaapp.data

data class EstadisticasTirador(
    val tirador: Usuario,
    val victorias: Int = 0,
    val derrotas: Int = 0,
    val tocadosDados: Int = 0,
    val tocadosRecibidos: Int = 0
) {
    val diferencia: Int get() = tocadosDados - tocadosRecibidos
    val porcentajeVictoria: Float get() = if ((victorias + derrotas) > 0)
        victorias.toFloat() / (victorias + derrotas) else 0f

    // Índice para el ranking: Victorias * 1000 + Diferencia (para desempatar)
    val indice: Int get() = (victorias * 1000) + diferencia
}