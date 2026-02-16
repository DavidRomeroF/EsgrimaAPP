package com.example.esgrimaapp.utilits

import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.data.Usuario

object SeedingLogic {

    // Función principal para generar TODO el cuadro inicial (Rondas T8, T4, T2...)
    fun generarCuadroCompleto(
        tiradores: List<EstadisticasTirador>,
        tamano: Int,
        arbitrosDisponibles: List<Usuario>
    ): List<Asalto> {
        val todosLosAsaltos = mutableListOf<Asalto>()
        val indices = generarIndicesSeeding(tamano)

        // 1. Generamos la PRIMERA RONDA (donde asignamos árbitros automáticamente)
        for (i in 0 until indices.size step 2) {
            val indexA = indices[i] - 1
            val indexB = indices[i + 1] - 1

            val tiradorA = if (indexA < tiradores.size) tiradores[indexA].tirador else Usuario("---", "")
            val tiradorB = if (indexB < tiradores.size) tiradores[indexB].tirador else Usuario("---", "")

            // Lógica de árbitro: Buscar uno que no sea del club de A ni de B
            val arbitroAsignado = if (tiradorA.nombre != "---" && tiradorB.nombre != "---") {
                val pool = arbitrosDisponibles.shuffled() // Mezclamos para que no sea siempre el mismo

                // INTENTO A: Alguien que no sea de ninguno de los dos clubes
                val ideal = pool.firstOrNull { arb ->
                    arb.club.isNullOrEmpty() || (arb.club != tiradorA.club && arb.club != tiradorB.club)
                }

                // INTENTO B: Si no hay nadie ideal, ponemos al primero que pillemos
                // "Mejor un árbitro del mismo club que dejar el asalto vacío"
                ideal ?: pool.firstOrNull()
            } else null

            val asaltoInicial = crearAsaltoInicial(
                id = "DE_${tamano}_${i / 2}",
                tA = tiradorA,
                tB = tiradorB,
                pista = (i / 2) + 1,
                arb = arbitroAsignado
            )

            // Gestionar pases directos inmediatos
            val asaltoProcesado = when {
                tiradorA.nombre != "---" && tiradorB.nombre == "---" -> {
                    asaltoInicial.copy(tocadosA = 1, tocadosB = 0, estado = EstadoAsalto.FINALIZADO, arbitro = null)
                }
                tiradorA.nombre == "---" && tiradorB.nombre != "---" -> {
                    asaltoInicial.copy(tocadosA = 0, tocadosB = 1, estado = EstadoAsalto.FINALIZADO, arbitro = null)
                }
                else -> asaltoInicial
            }
            todosLosAsaltos.add(asaltoProcesado)
        }

        // 2. Generamos el resto de rondas (T4, T2, etc.)
        var nivelRonda = tamano / 2
        while (nivelRonda >= 2) {
            val numAsaltosSiguienteRonda = nivelRonda / 2

            for (i in 0 until numAsaltosSiguienteRonda) {
                // BUSCAMOS LOS GANADORES DE LA RONDA ANTERIOR (Aquí se definen las variables que faltaban)
                val idAnteriorSup = "DE_${nivelRonda * 2}_${i * 2}"
                val idAnteriorInf = "DE_${nivelRonda * 2}_${i * 2 + 1}"

                val asaltoPrevioSup = todosLosAsaltos.find { it.id == idAnteriorSup }
                val asaltoPrevioInf = todosLosAsaltos.find { it.id == idAnteriorInf }

                // Si el asalto previo fue un BYE, el ganador ya sube con nombre
                val ganadorSup = if (asaltoPrevioSup?.estado == EstadoAsalto.FINALIZADO) {
                    if (asaltoPrevioSup.tocadosA > asaltoPrevioSup.tocadosB) asaltoPrevioSup.tiradorA else asaltoPrevioSup.tiradorB
                } else Usuario("---", "")

                val ganadorInf = if (asaltoPrevioInf?.estado == EstadoAsalto.FINALIZADO) {
                    if (asaltoPrevioInf.tocadosA > asaltoPrevioInf.tocadosB) asaltoPrevioInf.tiradorA else asaltoPrevioInf.tiradorB
                } else Usuario("---", "")

                // Añadimos el asalto de la siguiente ronda
                todosLosAsaltos.add(
                    crearAsaltoInicial(
                        id = "DE_${nivelRonda}_$i",
                        tA = ganadorSup,
                        tB = ganadorInf,
                        pista = 0,
                        arb = null // No asignamos árbitro aún porque no sabemos quién ganará los asaltos reales
                    )
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

    private fun crearAsaltoInicial(id: String, tA: Usuario, tB: Usuario, pista: Int, arb: Usuario? = null) = Asalto(
        id = id,
        grupoId = "FASE_FINAL",
        tiradorA = tA,
        tiradorB = tB,
        tocadosA = 0,
        tocadosB = 0,
        arbitro = arb, // Asignación aquí
        pista = pista,
        estado = EstadoAsalto.PROGRAMADO,
        horaEstimada = "--:--"
    )
}