package br.com.topoia

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.abs

/**
 * Cálculos de estação total.
 * Azimutes: graus, a partir do Norte, sentido horário.
 * Ângulos zenitais: graus a partir da vertical superior (0° no zênite, 90° no horizonte).
 * Distâncias e cotas: metros.
 */
object TotalStationCalculations {
    data class Coordinate(val east: Double, val north: Double, val elevation: Double? = null)
    data class Observation(
        val horizontalDistance: Double,
        val verticalDifference: Double,
        val slopeDistance: Double,
        val horizontalAngleDegrees: Double,
        val azimuthDegrees: Double,
        val target: Coordinate
    )
    data class Stakeout(
        val distance: Double,
        val azimuthDegrees: Double,
        val deltaEast: Double,
        val deltaNorth: Double
    )
    data class Intersection(
        val point: Coordinate,
        val distanceFromFirst: Double,
        val distanceFromSecond: Double
    )

    /** Irradiação por distância horizontal e azimute conhecido. */
    fun radiate(
        station: Coordinate,
        horizontalDistance: Double,
        azimuthDegrees: Double
    ): Coordinate {
        require(horizontalDistance.isFinite() && horizontalDistance >= 0.0)
        require(azimuthDegrees.isFinite())
        val az = Math.toRadians(normalizeAzimuth(azimuthDegrees))
        return Coordinate(
            station.east + horizontalDistance * sin(az),
            station.north + horizontalDistance * cos(az),
            station.elevation
        )
    }

    /** Irradiação usando leitura horizontal e azimute da ré (ângulo horário). */
    fun radiateByHorizontalAngle(
        station: Coordinate,
        backsightAzimuthDegrees: Double,
        horizontalCircleReadingDegrees: Double,
        horizontalDistance: Double
    ): Observation {
        val azimuth = normalizeAzimuth(backsightAzimuthDegrees + horizontalCircleReadingDegrees)
        val target = radiate(station, horizontalDistance, azimuth)
        return Observation(horizontalDistance, 0.0, horizontalDistance,
            horizontalCircleReadingDegrees, azimuth, target)
    }

    /** Reduz distância inclinada usando ângulo zenital e alturas do instrumento/prisma. */
    fun reduceZenithObservation(
        station: Coordinate,
        slopeDistance: Double,
        zenithAngleDegrees: Double,
        instrumentHeight: Double,
        prismHeight: Double,
        horizontalAngleDegrees: Double,
        backsightAzimuthDegrees: Double
    ): Observation {
        require(slopeDistance.isFinite() && slopeDistance >= 0.0)
        require(zenithAngleDegrees.isFinite() && zenithAngleDegrees in 0.0..180.0)
        require(instrumentHeight.isFinite() && prismHeight.isFinite())
        val z = Math.toRadians(zenithAngleDegrees)
        val horizontal = slopeDistance * sin(z)
        val vertical = slopeDistance * cos(z)
        val azimuth = normalizeAzimuth(backsightAzimuthDegrees + horizontalAngleDegrees)
        val plan = radiate(station, horizontal, azimuth)
        val elevation = station.elevation?.let { it + instrumentHeight + vertical - prismHeight }
        val target = plan.copy(elevation = elevation)
        return Observation(horizontal, vertical, slopeDistance, horizontalAngleDegrees, azimuth, target)
    }

    /** Locação: informa distância e azimute da estação até a coordenada de projeto. */
    fun stakeout(station: Coordinate, designPoint: Coordinate): Stakeout {
        val dE = designPoint.east - station.east
        val dN = designPoint.north - station.north
        val distance = sqrt(dE * dE + dN * dN)
        val azimuth = normalizeAzimuth(Math.toDegrees(kotlin.math.atan2(dE, dN)))
        return Stakeout(distance, azimuth, dE, dN)
    }

    /** Interseção de duas visadas horizontais (ponto inacessível). */
    fun intersectRays(
        firstStation: Coordinate,
        firstAzimuthDegrees: Double,
        secondStation: Coordinate,
        secondAzimuthDegrees: Double
    ): Intersection {
        require(firstAzimuthDegrees.isFinite() && secondAzimuthDegrees.isFinite())
        val a1 = Math.toRadians(normalizeAzimuth(firstAzimuthDegrees))
        val a2 = Math.toRadians(normalizeAzimuth(secondAzimuthDegrees))
        val v1e = sin(a1); val v1n = cos(a1)
        val v2e = sin(a2); val v2n = cos(a2)
        val denominator = v1e * v2n - v1n * v2e
        require(abs(denominator) > 1e-12) { "As visadas são paralelas ou coincidentes." }
        val dE = secondStation.east - firstStation.east
        val dN = secondStation.north - firstStation.north
        val t1 = (dE * v2n - dN * v2e) / denominator
        val t2 = (dE * v1n - dN * v1e) / denominator
        require(t1 >= 0.0 && t2 >= 0.0) { "A interseção não está à frente das duas estações." }
        val point = Coordinate(
            firstStation.east + t1 * v1e,
            firstStation.north + t1 * v1n,
            null
        )
        return Intersection(point, t1, t2)
    }

    private fun normalizeAzimuth(value: Double): Double = ((value % 360.0) + 360.0) % 360.0
}
