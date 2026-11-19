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

    data class AngularAdjustment(
        val adjustedAnglesDegrees: List<Double>,
        val angularMisclosureDegrees: Double,
        val correctionPerAngleDegrees: Double,
        val azimuthsDegrees: List<Double>,
        val coordinateResult: AdjustedResult
    )

    /**
     * Ajusta poligonal fechada a partir dos ângulos internos e distâncias.
     * interiorAngles[i] é o ângulo no vértice de início do lado i.
     * initialAzimuth é o azimute do primeiro lado; clockwise define o sentido do caminhamento.
     * A compensação angular é uniforme e a linear usa Bowditch.
     */
    fun closedFromInteriorAngles(
        start: Coordinate,
        distances: List<Double>,
        interiorAngles: List<Double>,
        initialAzimuthDegrees: Double,
        clockwise: Boolean = true
    ): AngularAdjustment {
        require(distances.size >= 3) { "Uma poligonal fechada precisa de pelo menos três lados." }
        require(distances.size == interiorAngles.size) { "Informe um ângulo interno por vértice." }
        require(distances.all { it > 0.0 && it.isFinite() }) { "Todas as distâncias devem ser positivas." }
        require(interiorAngles.all { it > 0.0 && it < 360.0 && it.isFinite() }) { "Ângulo interno inválido." }
        require(initialAzimuthDegrees.isFinite()) { "Azimute inicial inválido." }

        val count = distances.size
        val theoreticalSum = (count - 2) * 180.0
        val observedSum = interiorAngles.sum()
        val misclosure = observedSum - theoreticalSum
        val correction = -misclosure / count
        val adjusted = interiorAngles.map { it + correction }
        require(adjusted.all { it > 0.0 && it < 360.0 }) { "A compensação gerou ângulo inválido." }

        val direction = if (clockwise) 1.0 else -1.0
        val azimuths = MutableList(count) { 0.0 }
        azimuths[0] = normalizeAzimuth(initialAzimuthDegrees)
        for (sideIndex in 1 until count) {
            val angleAtVertex = adjusted[sideIndex]
            azimuths[sideIndex] = normalizeAzimuth(
                azimuths[sideIndex - 1] + direction * (180.0 - angleAtVertex)
            )
        }

        val sides = distances.indices.map { i ->
            Side("P${i + 1}", distances[i], azimuths[i])
        }
        val linear = bowditch(start, sides, start)
        return AngularAdjustment(adjusted, misclosure, correction, azimuths, linear)
    }

    data class OpenAdjustment(
        val azimuthsDegrees: List<Double>,
        val coordinateResult: AdjustedResult
    )

    /**
     * Poligonal aberta enquadrada por coordenadas conhecidas no início e no fim.
     * Deflexões são assinadas: positivas para a direita (horário), negativas para a esquerda.
     * A primeira direção é o azimute inicial; cada deflexão orienta o lado seguinte.
     * O fechamento linear é compensado pelo método de Bowditch.
     */
    fun openFromDeflectionAngles(
        start: Coordinate,
        end: Coordinate,
        distances: List<Double>,
        deflectionsDegrees: List<Double>,
        initialAzimuthDegrees: Double
    ): OpenAdjustment {
        require(distances.isNotEmpty()) { "Informe pelo menos um lado." }
        require(deflectionsDegrees.size == distances.size - 1) {
            "Informe uma deflexão entre cada par de lados consecutivos."
        }
        require(distances.all { it.isFinite() && it > 0.0 }) { "Distâncias devem ser positivas e finitas." }
        require(deflectionsDegrees.all { it.isFinite() }) { "Deflexões devem ser finitas." }
        require(initialAzimuthDegrees.isFinite()) { "Azimute inicial inválido." }
        require(start.easting.isFinite() && start.northing.isFinite() &&
            end.easting.isFinite() && end.northing.isFinite()) { "Coordenadas de apoio inválidas." }

        val azimuths = MutableList(distances.size) { 0.0 }
        azimuths[0] = normalizeAzimuth(initialAzimuthDegrees)
        for (i in 1 until distances.size) {
            azimuths[i] = normalizeAzimuth(azimuths[i - 1] + deflectionsDegrees[i - 1])
        }
        val sides = distances.indices.map { i ->
            Side("P${i + 1}", distances[i], azimuths[i])
        }
        return OpenAdjustment(azimuths, bowditch(start, sides, end))
    }

    private fun normalizeAzimuth(value: Double): Double = (value % 360.0 + 360.0) % 360.0

    fun azimuthFromCoordinates(e1: Double, n1: Double, e2: Double, n2: Double): Double {
        return (Math.toDegrees(atan2(e2 - e1, n2 - n1)) + 360.0) % 360.0
    }
}
