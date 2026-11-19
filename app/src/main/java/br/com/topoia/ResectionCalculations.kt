package br.com.topoia

import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Resseção planimétrica por distâncias (trilateração) com ajustamento iterativo.
 * Coordenadas e distâncias em metros. Exige ao menos três pontos de controle.
 */
object ResectionCalculations {
    data class DistanceObservation(
        val controlName: String,
        val east: Double,
        val north: Double,
        val measuredDistance: Double,
        val standardDeviation: Double = 1.0
    )

    data class Result(
        val east: Double,
        val north: Double,
        val iterations: Int,
        val converged: Boolean,
        val rmsResidual: Double,
        val residuals: List<Pair<String, Double>>
    )

    fun byDistances(
        observations: List<DistanceObservation>,
        initialEast: Double,
        initialNorth: Double,
        maxIterations: Int = 100,
        coordinateTolerance: Double = 1e-8
    ): Result {
        require(observations.size >= 3) { "Informe pelo menos três pontos de apoio." }
        require(initialEast.isFinite() && initialNorth.isFinite()) { "Coordenada inicial inválida." }
        require(maxIterations > 0 && coordinateTolerance > 0.0 && coordinateTolerance.isFinite())
        observations.forEach {
            require(it.east.isFinite() && it.north.isFinite()) { "Coordenada de apoio inválida." }
            require(it.measuredDistance.isFinite() && it.measuredDistance > 0.0) { "Distância medida deve ser positiva." }
            require(it.standardDeviation.isFinite() && it.standardDeviation > 0.0) { "Desvio-padrão deve ser positivo." }
        }
        require(observations.map { it.controlName }.distinct().size == observations.size) {
            "Os nomes dos pontos de apoio devem ser únicos."
        }

        var e = initialEast
        var n = initialNorth
        var converged = false
        var iterations = 0

        for (iteration in 1..maxIterations) {
            var n11 = 0.0; var n12 = 0.0; var n22 = 0.0
            var u1 = 0.0; var u2 = 0.0
            observations.forEach { obs ->
                val de = e - obs.east
                val dn = n - obs.north
                val computed = hypot(de, dn)
                require(computed > 1e-10) { "Estimativa coincide com um ponto de apoio; altere a coordenada inicial." }
                val residual = obs.measuredDistance - computed
                val a = de / computed
                val b = dn / computed
                val weight = 1.0 / (obs.standardDeviation * obs.standardDeviation)
                n11 += weight * a * a
                n12 += weight * a * b
                n22 += weight * b * b
                u1 += weight * a * residual
                u2 += weight * b * residual
            }
            val determinant = n11 * n22 - n12 * n12
            require(determinant > 1e-14) {
                "Geometria dos pontos de apoio insuficiente ou quase colinear."
            }
            val deltaE = (u1 * n22 - u2 * n12) / determinant
            val deltaN = (u2 * n11 - u1 * n12) / determinant
            e += deltaE
            n += deltaN
            iterations = iteration
            if (hypot(deltaE, deltaN) <= coordinateTolerance) {
                converged = true
                break
            }
        }

        val residuals = observations.map { obs ->
            obs.controlName to (hypot(e - obs.east, n - obs.north) - obs.measuredDistance)
        }
        val sumSquares = residuals.sumOf { it.second * it.second }
        val rms = sqrt(sumSquares / residuals.size)
        return Result(e, n, iterations, converged, rms, residuals)
    }
}
