package br.com.topoia

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Núcleo matemático inicial do TOPO IA.
 * Convenção: coordenadas planas E (Este) e N (Norte), azimute contado
 * a partir do Norte no sentido horário, em graus.
 */
object TopoCalculations {
    data class InverseResult(val distance: Double, val azimuthDegrees: Double)

    fun inverse(e1: Double, n1: Double, e2: Double, n2: Double): InverseResult {
        val dE = e2 - e1
        val dN = n2 - n1
        val distance = hypot(dE, dN)
        val azimuth = (Math.toDegrees(atan2(dE, dN)) + 360.0) % 360.0
        return InverseResult(distance, azimuth)
    }

    fun forward(e: Double, n: Double, distance: Double, azimuthDegrees: Double): Pair<Double, Double> {
        require(distance >= 0.0) { "A distância não pode ser negativa." }
        val azimuth = Math.toRadians(azimuthDegrees)
        return Pair(
            e + distance * sin(azimuth),
            n + distance * cos(azimuth)
        )
    }

    data class LevelResult(val instrumentHeight: Double, val pointElevation: Double, val difference: Double)

    fun leveling(knownElevation: Double, backsight: Double, foresight: Double): LevelResult {
        val hi = knownElevation + backsight
        val elevation = hi - foresight
        return LevelResult(hi, elevation, elevation - knownElevation)
    }

    fun stationNumber(chainageMeters: Double, intervalMeters: Double = 20.0): Pair<Int, Double> {
        require(chainageMeters >= 0.0) { "A distância acumulada não pode ser negativa." }
        require(intervalMeters > 0.0) { "O intervalo deve ser maior que zero." }
        val whole = (chainageMeters / intervalMeters).toInt()
        val remainder = chainageMeters - whole * intervalMeters
        return Pair(whole, remainder)
    }
}
