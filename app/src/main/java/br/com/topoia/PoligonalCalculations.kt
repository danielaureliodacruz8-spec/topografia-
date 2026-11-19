package br.com.topoia

import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.cos

/** Poligonal plana por azimutes e distâncias. Azimute contado do Norte, horário. */
object PoligonalCalculations {
    data class Side(val name: String, val distance: Double, val azimuthDegrees: Double)
    data class Coordinate(val name: String, val easting: Double, val northing: Double)
    data class AdjustedResult(
        val coordinates: List<Coordinate>, val sumDistances: Double,
        val deltaE: Double, val deltaN: Double, val linearMisclosure: Double,
        val relativePrecision: String
    )

    /** Compensação Bowditch para poligonal fechada (end=start) ou enquadrada. */
    fun bowditch(start: Coordinate, sides: List<Side>, end: Coordinate = start): AdjustedResult {
        require(sides.isNotEmpty()) { "Informe pelo menos um lado." }
        sides.forEach {
            require(it.distance > 0.0 && it.distance.isFinite()) { "Distância inválida." }
            require(it.azimuthDegrees.isFinite()) { "Azimute inválido." }
        }
        val increments = sides.map {
            val az = Math.toRadians((it.azimuthDegrees % 360.0 + 360.0) % 360.0)
            Pair(it.distance * sin(az), it.distance * cos(az))
        }
        val perimeter = sides.sumOf { it.distance }
        val sumE = increments.sumOf { it.first }
        val sumN = increments.sumOf { it.second }
        val errorE = end.easting - (start.easting + sumE)
        val errorN = end.northing - (start.northing + sumN)
        val error = hypot(errorE, errorN)
        var e = start.easting
        var n = start.northing
        val points = mutableListOf(start)
        sides.forEachIndexed { i, side ->
            e += increments[i].first + errorE * side.distance / perimeter
            n += increments[i].second + errorN * side.distance / perimeter
            points += Coordinate(side.name.ifBlank { "P${i + 1}" }, e, n)
        }
        points[points.lastIndex] = end
        val precision = if (error == 0.0) "Fechamento matemático"
            else "1:${(perimeter / error).toLong().coerceAtLeast(1)}"
        return AdjustedResult(points, perimeter, errorE, errorN, error, precision)
    }

    fun azimuthFromCoordinates(e1: Double, n1: Double, e2: Double, n2: Double): Double {
        return (Math.toDegrees(atan2(e2 - e1, n2 - n1)) + 360.0) % 360.0
    }
}
