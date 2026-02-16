package com.example.esgrimaapp.utilits

import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.data.Usuario

object SeedingLogic {

    // Función principal para generar TODO el cuadro inicial (Rondas T8, T4, T2...)
    fun generarCuadroCompleto(tiradores: List<EstadisticasTirador>, tamano: Int): List<Asalto> {
        val todosLosAsaltos = mutableListOf<Asalto>()
        val indices = generarIndicesSeeding(tamano)

        // 1. Generamos la PRIMERA RONDA
        for (i in 0 until indices.size step 2) {
            val indexA = indices[i] - 1
            val indexB = indices[i + 1] - 1

            val tiradorA = if (indexA < tiradores.size) tiradores[indexA].tirador else Usuario("---", "")
            val tiradorB = if (indexB < tiradores.size) tiradores[indexB].tirador else Usuario("---", "")

            val asaltoInicial = crearAsaltoInicial(id = "DE_${tamano}_${i / 2}", tA = tiradorA, tB = tiradorB, pista = (i / 2) + 1)

            // LÓGICA DE PASE DIRECTO: Si uno es "---", el asalto nace ya FINALIZADO
            val asaltoProcesado = when {
                tiradorA.nombre != "---" && tiradorB.nombre == "---" -> {
                    asaltoInicial.copy(tocadosA = 1, tocadosB = 0, estado = EstadoAsalto.FINALIZADO)
                }
                tiradorA.nombre == "---" && tiradorB.nombre != "---" -> {
                    asaltoInicial.copy(tocadosA = 0, tocadosB = 1, estado = EstadoAsalto.FINALIZADO)
                }
                else -> asaltoInicial
            }
            todosLosAsaltos.add(asaltoProcesado)
        }

        // 2. Generamos los HUECOS y PROMOVEMOS a los que tienen pase directo
        var nivelRonda = tamano / 2
        while (nivelRonda >= 2) {
            val numAsaltosRondaActual = nivelRonda
            val numAsaltosSiguienteRonda = nivelRonda / 2

            for (i in 0 until numAsaltosSiguienteRonda) {
                // Buscamos si en la ronda anterior alguien ya ganó por pase directo para subirlo
                val asaltoPrevioSuperior = todosLosAsaltos.find { it.id == "DE_${nivelRonda * 2}_${i * 2}" }
                val asaltoPrevioInferior = todosLosAsaltos.find { it.id == "DE_${nivelRonda * 2}_${i * 2 + 1}" }

                val ganadorSup = if (asaltoPrevioSuperior?.estado == EstadoAsalto.FINALIZADO) {
                    if (asaltoPrevioSuperior.tocadosA > asaltoPrevioSuperior.tocadosB) asaltoPrevioSuperior.tiradorA else asaltoPrevioSuperior.tiradorB
                } else Usuario("---", "")

                val ganadorInf = if (asaltoPrevioInferior?.estado == EstadoAsalto.FINALIZADO) {
                    if (asaltoPrevioInferior.tocadosA > asaltoPrevioInferior.tocadosB) asaltoPrevioInferior.tiradorA else asaltoPrevioInferior.tiradorB
                } else Usuario("---", "")

                todosLosAsaltos.add(
                    crearAsaltoInicial(id = "DE_${nivelRonda}_$i", tA = ganadorSup, tB = ganadorInf, pista = 0)
                )
            }
            nivelRonda /= 2
        }
        return todosLosAsaltos
    }

    // Esta es la función que te faltaba: La lógica oficial de cruces FIE
    private fun generarIndicesSeeding(n: Int): List<Int> {
        return when (n) {
            8 -> listOf(1, 8, 5, 4, 3, 6, 7, 2)
            16 -> listOf(1, 16, 9, 8, 5, 12, 13, 4, 3, 14, 11, 6, 7, 10, 15, 2)
            32 -> listOf(
                1, 32, 17, 16, 9, 24, 25, 8, 5, 28, 21, 12, 13, 20, 29, 4,
                3, 30, 19, 14, 11, 22, 27, 6, 7, 26, 23, 10, 15, 18, 31, 2
            )
            else -> emptyList()
        }
    }

    private fun crearAsaltoInicial(id: String, tA: Usuario, tB: Usuario, pista: Int) = Asalto(
        id = id,
        grupoId = "FASE_FINAL",
        tiradorA = tA,
        tiradorB = tB,
        tocadosA = 0,
        tocadosB = 0,
        arbitro = null,
        pista = pista,
        estado = EstadoAsalto.PROGRAMADO,
        horaEstimada = "--:--"
    )
}