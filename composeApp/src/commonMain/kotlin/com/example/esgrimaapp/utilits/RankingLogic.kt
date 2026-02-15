package com.example.esgrimaapp.utilits

import com.example.esgrimaapp.data.Asalto
import com.example.esgrimaapp.data.EstadisticasTirador
import com.example.esgrimaapp.data.EstadoAsalto
import com.example.esgrimaapp.data.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object RankingLogic {
    // Cambiamos a una función normal que devuelve el Mapa
    fun procesar(
        asaltosMap: Map<String, List<Asalto>>,
        inscripcionesMap: Map<String, List<Usuario>>
    ): Map<String, List<EstadisticasTirador>> {

        return inscripcionesMap.mapValues { (compId, tiradores) ->
            val asaltos = asaltosMap[compId] ?: emptyList()
            val statsMap = tiradores.associateWith { EstadisticasTirador(it) }.toMutableMap()

            asaltos.filter { it.estado == EstadoAsalto.FINALIZADO }.forEach { asalto ->
                val sA = statsMap[asalto.tiradorA]
                val sB = statsMap[asalto.tiradorB]

                if (sA != null && sB != null) {
                    statsMap[asalto.tiradorA] = sA.copy(
                        victorias = sA.victorias + if (asalto.tocadosA > asalto.tocadosB) 1 else 0,
                        derrotas = sA.derrotas + if (asalto.tocadosA < asalto.tocadosB) 1 else 0,
                        tocadosDados = sA.tocadosDados + asalto.tocadosA,
                        tocadosRecibidos = sA.tocadosRecibidos + asalto.tocadosB
                    )
                    statsMap[asalto.tiradorB] = sB.copy(
                        victorias = sB.victorias + if (asalto.tocadosB > asalto.tocadosA) 1 else 0,
                        derrotas = sB.derrotas + if (asalto.tocadosB < asalto.tocadosA) 1 else 0,
                        tocadosDados = sB.tocadosDados + asalto.tocadosB,
                        tocadosRecibidos = sB.tocadosRecibidos + asalto.tocadosA
                    )
                }
            }
            statsMap.values.sortedByDescending { it.indice }
        }
    }
}