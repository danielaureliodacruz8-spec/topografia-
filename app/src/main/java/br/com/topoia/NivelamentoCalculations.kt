package br.com.topoia

/** Nivelamento geométrico composto por sequência de ré/vante. */
object NivelamentoCalculations {
    data class Setup(val backsight: Double, val foresight: Double, val distanceMeters: Double = 0.0, val label: String = "")
    data class SetupResult(val label: String, val instrumentHeight: Double, val nextElevation: Double, val difference: Double, val distanceMeters: Double)
    data class RunResult(val setups: List<SetupResult>, val finalElevation: Double, val totalDistanceMeters: Double)

    fun calculate(startElevation: Double, setups: List<Setup>): RunResult {
        require(setups.isNotEmpty()) { "Informe pelo menos uma estação." }
        require(startElevation.isFinite())
        var current = startElevation
        val results = setups.mapIndexed { index, setup ->
            require(setup.backsight >= 0.0 && setup.foresight >= 0.0 &&
                setup.backsight.isFinite() && setup.foresight.isFinite()) { "Leitura inválida." }
            require(setup.distanceMeters >= 0.0 && setup.distanceMeters.isFinite()) { "Distância inválida." }
            val hi = current + setup.backsight
            val next = hi - setup.foresight
            val row = SetupResult(setup.label.ifBlank { "Estação ${index + 1}" }, hi, next, next - current, setup.distanceMeters)
            current = next
            row
        }
        return RunResult(results, current, setups.sumOf { it.distanceMeters })
    }

    /** Compensa cotas proporcionalmente às distâncias acumuladas; erro = cota calculada - conhecida. */
    fun compensateByDistance(elevations: List<Double>, cumulativeDistances: List<Double>, closingError: Double): List<Double> {
        require(elevations.isNotEmpty() && elevations.size == cumulativeDistances.size)
        require(cumulativeDistances.all { it >= 0.0 })
        val total = cumulativeDistances.last()
        require(total > 0.0) { "Distância total deve ser positiva." }
        return elevations.mapIndexed { i, elevation -> elevation - closingError * cumulativeDistances[i] / total }
    }
}
