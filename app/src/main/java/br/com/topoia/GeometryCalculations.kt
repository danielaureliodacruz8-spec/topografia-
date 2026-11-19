package br.com.topoia

import kotlin.math.*

/** Fórmulas geométricas topográficas. Unidades devem ser coerentes e informadas pelo usuário. */
object GeometryCalculations {
    data class SlopeResult(val horizontalDistance: Double, val verticalDifference: Double, val slopePercent: Double, val angleDegrees: Double)
    data class SlopeDistanceResult(val horizontal: Double, val vertical: Double)
    data class AreaResult(val signedArea: Double, val area: Double, val perimeter: Double)

    fun slope(horizontalDistance: Double, verticalDifference: Double): SlopeResult {
        require(horizontalDistance > 0.0 && horizontalDistance.isFinite())
        require(verticalDifference.isFinite())
        return SlopeResult(horizontalDistance, verticalDifference,
            verticalDifference / horizontalDistance * 100.0,
            Math.toDegrees(atan2(verticalDifference, horizontalDistance)))
    }

    /** Reduz distância inclinada e ângulo vertical contado a partir do horizonte. */
    fun reduceSlopeDistance(slopeDistance: Double, verticalAngleDegrees: Double): SlopeDistanceResult {
        require(slopeDistance >= 0.0 && slopeDistance.isFinite())
        require(verticalAngleDegrees.isFinite() && verticalAngleDegrees in -90.0..90.0)
        val angle = Math.toRadians(verticalAngleDegrees)
        return SlopeDistanceResult(slopeDistance * cos(angle), slopeDistance * sin(angle))
    }

    /** Área pelo método das coordenadas (Shoelace), com pontos em ordem de contorno. */
    fun polygonArea(points: List<Pair<Double, Double>>): AreaResult {
        require(points.size >= 3) { "Informe pelo menos três vértices." }
        val closed = if (points.first() == points.last()) points else points + points.first()
        var twiceArea = 0.0
        var perimeter = 0.0
        for (i in 0 until closed.lastIndex) {
            val (x1, y1) = closed[i]
            val (x2, y2) = closed[i + 1]
            require(x1.isFinite() && y1.isFinite() && x2.isFinite() && y2.isFinite())
            twiceArea += x1 * y2 - x2 * y1
            perimeter += hypot(x2 - x1, y2 - y1)
        }
        val signed = twiceArea / 2.0
        return AreaResult(signed, abs(signed), perimeter)
    }

    fun interpolate(start: Double, end: Double, fraction: Double): Double {
        require(fraction in 0.0..1.0)
        return start + (end - start) * fraction
    }

    /** Estaca decimal: distância acumulada dividida pelo intervalo. */
    fun chainage(distanceMeters: Double, intervalMeters: Double = 20.0): Pair<Int, Double> {
        require(distanceMeters >= 0.0 && distanceMeters.isFinite())
        require(intervalMeters > 0.0 && intervalMeters.isFinite())
        val station = floor(distanceMeters / intervalMeters).toInt()
        return station to (distanceMeters - station * intervalMeters)
    }

    fun formatStation(distanceMeters: Double, intervalMeters: Double = 20.0): String {
        val (station, offset) = chainage(distanceMeters, intervalMeters)
        return "$station + " + String.format(java.util.Locale.US, "%.3f", offset)
    }
}
