package br.com.topoia

import kotlin.math.abs

/**
 * Geometria básica de seção rodoviária e encontro do talude com o terreno.
 * Distâncias em metros; cotas em metros; inclinações transversais em porcentagem.
 * O sinal do caimento é positivo quando a plataforma desce para fora do eixo.
 */
object RoadSectionCalculations {
    data class GroundPoint(val outwardDistance: Double, val elevation: Double)
    data class Edge(val offset: Double, val elevation: Double)
    data class Daylight(
        val offset: Double,
        val elevation: Double,
        val type: String,
        val horizontalFromEdge: Double
    )
    data class SideResult(val edge: Edge, val daylight: Daylight?)

    /** Calcula a borda esquerda/direita a partir da cota do eixo e do caimento informado. */
    fun platformEdge(
        axisElevation: Double,
        halfWidth: Double,
        crossfallPercent: Double,
        side: Int
    ): Edge {
        require(axisElevation.isFinite() && halfWidth.isFinite() && crossfallPercent.isFinite())
        require(halfWidth > 0.0) { "A meia largura deve ser maior que zero." }
        require(side == -1 || side == 1) { "Lado deve ser -1 (esquerda) ou 1 (direita)." }
        val elevation = axisElevation - abs(crossfallPercent) / 100.0 * halfWidth
        return Edge(side * halfWidth, elevation)
    }

    /**
     * Encontra o off-set de encontro do talude com o terreno.
     * ground deve começar na borda da plataforma e seguir para fora, em distâncias crescentes.
     * sideSlopeHOverV representa H:V (ex.: 1.5 significa 1,5H:1V).
     * A diferença projeto-terreno na borda define corte ou aterro.
     */
    fun daylight(
        edge: Edge,
        ground: List<GroundPoint>,
        sideSlopeHOverV: Double,
        side: Int
    ): Daylight? {
        require(side == -1 || side == 1)
        require(sideSlopeHOverV.isFinite() && sideSlopeHOverV > 0.0) {
            "Informe a relação horizontal/vertical do talude maior que zero."
        }
        require(ground.size >= 2) { "Informe pelo menos dois pontos do terreno além da borda." }
        require(ground.all { it.outwardDistance.isFinite() && it.elevation.isFinite() })
        require(ground.zipWithNext().all { it.second.outwardDistance > it.first.outwardDistance }) {
            "As distâncias do terreno devem estar em ordem crescente."
        }
        require(abs(ground.first().outwardDistance - abs(edge.offset)) <= 1e-6) {
            "O primeiro ponto do terreno deve coincidir com a borda da plataforma."
        }

        val firstDifference = edge.elevation - ground.first().elevation
        if (abs(firstDifference) <= 1e-9) {
            return Daylight(edge.offset, edge.elevation, "Encontro na borda", 0.0)
        }
        val fill = firstDifference > 0.0
        fun difference(point: GroundPoint): Double {
            val run = point.outwardDistance - ground.first().outwardDistance
            val design = edge.elevation + (if (fill) -1.0 else 1.0) * run / sideSlopeHOverV
            return design - point.elevation
        }

        for (i in 0 until ground.lastIndex) {
            val a = ground[i]
            val b = ground[i + 1]
            val da = difference(a)
            val db = difference(b)
            if (abs(da) <= 1e-9 || da * db <= 0.0) {
                val fraction = if (abs(da - db) <= 1e-12) 0.0 else da / (da - db)
                val distance = a.outwardDistance + fraction * (b.outwardDistance - a.outwardDistance)
                val elevation = a.elevation + fraction * (b.elevation - a.elevation)
                return Daylight(
                    side * distance,
                    elevation,
                    if (fill) "Aterro" else "Corte",
                    distance - ground.first().outwardDistance
                )
            }
        }
        return null
    }

    fun side(
        axisElevation: Double,
        halfWidth: Double,
        crossfallPercent: Double,
        side: Int,
        ground: List<GroundPoint>,
        sideSlopeHOverV: Double
    ): SideResult {
        val edge = platformEdge(axisElevation, halfWidth, crossfallPercent, side)
        return SideResult(edge, daylight(edge, ground, sideSlopeHOverV, side))
    }
}
