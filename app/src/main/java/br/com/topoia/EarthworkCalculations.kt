package br.com.topoia

import kotlin.math.abs

/**
 * Seções transversais e volumes de terraplenagem.
 * Ordenadas: projeto - terreno natural (m). Positivo = aterro; negativo = corte.
 * Offsets devem ser medidos a partir do eixo, em metros, da esquerda para a direita.
 */
object EarthworkCalculations {
    data class SectionAreas(val cutArea: Double, val fillArea: Double)
    data class Section(val station: Double, val areas: SectionAreas)
    data class VolumeResult(
        val length: Double,
        val cutVolume: Double,
        val fillVolume: Double,
        val method: String
    )
    data class CumulativeResult(
        val segments: List<VolumeResult>,
        val cumulativeCut: List<Double>,
        val cumulativeFill: List<Double>
    )

    /** Integra as diferenças por trapézios, interpolando cruzamentos entre corte e aterro. */
    fun sectionAreas(offsets: List<Double>, designMinusGround: List<Double>): SectionAreas {
        require(offsets.size == designMinusGround.size && offsets.size >= 2) {
            "Informe ao menos dois offsets e uma ordenada correspondente para cada um."
        }
        require(offsets.all { it.isFinite() } && designMinusGround.all { it.isFinite() }) {
            "Offsets e ordenadas devem ser valores finitos."
        }
        require(offsets.zipWithNext().all { it.second > it.first }) {
            "Offsets devem estar em ordem crescente, sem repetição."
        }

        var cut = 0.0
        var fill = 0.0
        for (i in 0 until offsets.lastIndex) {
            val width = offsets[i + 1] - offsets[i]
            val a = designMinusGround[i]
            val b = designMinusGround[i + 1]
            when {
                a >= 0.0 && b >= 0.0 -> fill += width * (a + b) / 2.0
                a <= 0.0 && b <= 0.0 -> cut += width * (abs(a) + abs(b)) / 2.0
                else -> {
                    val fraction = abs(a) / (abs(a) + abs(b))
                    val leftWidth = width * fraction
                    val rightWidth = width - leftWidth
                    if (a < 0.0) {
                        cut += leftWidth * abs(a) / 2.0
                        fill += rightWidth * b / 2.0
                    } else {
                        fill += leftWidth * a / 2.0
                        cut += rightWidth * abs(b) / 2.0
                    }
                }
            }
        }
        return SectionAreas(cut, fill)
    }

    /** Método das áreas médias entre duas seções consecutivas. */
    fun averageEndArea(first: Section, second: Section): VolumeResult {
        val length = second.station - first.station
        require(length.isFinite() && length > 0.0) { "As estacas devem estar em ordem crescente." }
        return VolumeResult(
            length,
            length * (first.areas.cutArea + second.areas.cutArea) / 2.0,
            length * (first.areas.fillArea + second.areas.fillArea) / 2.0,
            "Áreas médias"
        )
    }

    /** Método prismoidal; middle deve representar a seção exatamente no meio do intervalo. */
    fun prismoidal(first: Section, middle: Section, second: Section): VolumeResult {
        val length = second.station - first.station
        require(length.isFinite() && length > 0.0) { "As estacas devem estar em ordem crescente." }
        require(abs(middle.station - (first.station + second.station) / 2.0) <= 1e-6) {
            "A seção intermediária deve estar exatamente no meio do intervalo."
        }
        return VolumeResult(
            length,
            length / 6.0 * (first.areas.cutArea + 4.0 * middle.areas.cutArea + second.areas.cutArea),
            length / 6.0 * (first.areas.fillArea + 4.0 * middle.areas.fillArea + second.areas.fillArea),
            "Prismoidal"
        )
    }

    /** Calcula volumes entre seções consecutivas e acumulados separados de corte e aterro. */
    fun cumulative(sections: List<Section>): CumulativeResult {
        require(sections.size >= 2) { "Informe pelo menos duas seções." }
        require(sections.all { it.station.isFinite() }) { "Estaca inválida." }
        val segments = sections.zipWithNext { a, b -> averageEndArea(a, b) }
        var cut = 0.0
        var fill = 0.0
        val cuts = mutableListOf(0.0)
        val fills = mutableListOf(0.0)
        segments.forEach {
            cut += it.cutVolume
            fill += it.fillVolume
            cuts += cut
            fills += fill
        }
        return CumulativeResult(segments, cuts, fills)
    }
}
