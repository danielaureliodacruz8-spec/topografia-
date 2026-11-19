package br.com.topoia

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/** Geometria rodoviária. Distâncias/estaqueamento em metros; ângulos em graus. */
object RoadGeometryCalculations {
    data class HorizontalCurve(
        val radius: Double,
        val deflectionDegrees: Double,
        val tangent: Double,
        val development: Double,
        val longChord: Double,
        val external: Double,
        val middleOrdinate: Double,
        val pcStation: Double,
        val ptStation: Double
    )

    /** Curva circular simples. delta é o ângulo central positivo entre 0 e 180 graus. */
    fun horizontalCurve(radius: Double, deflectionDegrees: Double, piStation: Double): HorizontalCurve {
        require(radius.isFinite() && radius > 0.0) { "Raio deve ser positivo." }
        require(deflectionDegrees.isFinite() && deflectionDegrees > 0.0 && deflectionDegrees < 180.0) {
            "Ângulo central deve estar entre 0 e 180 graus."
        }
        require(piStation.isFinite()) { "Estaca do PI inválida." }

        val halfRadians = Math.toRadians(deflectionDegrees / 2.0)
        val tangent = radius * tan(halfRadians)
        val development = PI * radius * deflectionDegrees / 180.0
        val longChord = 2.0 * radius * sin(halfRadians)
        val external = radius * (1.0 / cos(halfRadians) - 1.0)
        val middleOrdinate = radius * (1.0 - cos(halfRadians))
        val pc = piStation - tangent
        return HorizontalCurve(radius, deflectionDegrees, tangent, development, longChord,
            external, middleOrdinate, pc, pc + development)
    }

    data class VerticalCurve(
        val length: Double,
        val incomingGradePercent: Double,
        val outgoingGradePercent: Double,
        val algebraicDifferencePercent: Double,
        val kValue: Double,
        val pvcStation: Double,
        val pviStation: Double,
        val pvtStation: Double,
        val pvcElevation: Double,
        val pviElevation: Double,
        val pvtElevation: Double
    )

    /** Curva parabólica simétrica. Rampas em %, cotas em metros e estaqueamento em metros. */
    fun verticalCurve(
        length: Double,
        incomingGradePercent: Double,
        outgoingGradePercent: Double,
        pviStation: Double,
        pviElevation: Double
    ): VerticalCurve {
        require(length.isFinite() && length > 0.0) { "Comprimento deve ser positivo." }
        require(incomingGradePercent.isFinite() && outgoingGradePercent.isFinite()) { "Rampa inválida." }
        require(pviStation.isFinite() && pviElevation.isFinite()) { "Dados do PIV inválidos." }

        val half = length / 2.0
        val pvcStation = pviStation - half
        val pvtStation = pviStation + half
        val pvcElevation = pviElevation - (incomingGradePercent / 100.0) * half
        val pvtElevation = pvcElevation + (incomingGradePercent / 100.0) * length +
            ((outgoingGradePercent - incomingGradePercent) / 100.0) * length / 2.0
        val difference = abs(outgoingGradePercent - incomingGradePercent)
        val k = if (difference == 0.0) Double.POSITIVE_INFINITY else length / difference
        return VerticalCurve(length, incomingGradePercent, outgoingGradePercent, difference, k,
            pvcStation, pviStation, pvtStation, pvcElevation, pviElevation, pvtElevation)
    }

    /** Cota na parábola a partir do PVC; x deve estar entre 0 e L, inclusive. */
    fun verticalElevation(curve: VerticalCurve, distanceFromPvc: Double): Double {
        require(distanceFromPvc.isFinite() && distanceFromPvc >= 0.0 &&
            distanceFromPvc <= curve.length) { "Distância fora dos limites PVC-PVT." }
        val x = distanceFromPvc
        val g1 = curve.incomingGradePercent / 100.0
        val gradeChange = (curve.outgoingGradePercent - curve.incomingGradePercent) / 100.0
        return curve.pvcElevation + g1 * x + gradeChange * x * x / (2.0 * curve.length)
    }

    /** Converte distância acumulada em estaca + avanço, respeitando intervalo configurável. */
    fun stationLabel(chainage: Double, interval: Double = 20.0): String {
        require(chainage.isFinite() && chainage >= 0.0) { "Distância acumulada inválida." }
        require(interval.isFinite() && interval > 0.0) { "Intervalo de estaca inválido." }
        val major = kotlin.math.floor(chainage / interval).toLong()
        val remainder = chainage - major * interval
        return "${major}+${"%.3f".format(java.util.Locale.US, remainder)}"
    }
}
