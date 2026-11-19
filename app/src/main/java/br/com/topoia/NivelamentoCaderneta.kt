package br.com.topoia

/**
 * Caderneta de nivelamento geométrico.
 * Cada estação contém uma leitura de ré, visadas intermediárias opcionais e uma vante
 * (normalmente ponto de mudança, exceto na última estação).
 */
object NivelamentoCaderneta {
    data class IntermediateSight(val point: String, val reading: Double)
    data class Station(
        val backsightPoint: String,
        val backsight: Double,
        val intermediates: List<IntermediateSight> = emptyList(),
        val foresightPoint: String,
        val foresight: Double,
        val distanceMeters: Double = 0.0
    )
    data class PointElevation(val point: String, val elevation: Double, val instrumentHeight: Double, val type: String)
    data class Result(
        val points: List<PointElevation>,
        val finalElevation: Double,
        val totalDistanceMeters: Double,
        val sumBacksights: Double,
        val sumForesights: Double,
        val arithmeticCheck: Double
    )

    fun calculate(startPoint: String, startElevation: Double, stations: List<Station>): Result {
        require(startPoint.isNotBlank()) { "Informe o ponto inicial." }
        require(startElevation.isFinite()) { "Cota inicial inválida." }
        require(stations.isNotEmpty()) { "Informe pelo menos uma estação." }

        var currentElevation = startElevation
        var sumBS = 0.0
        var sumFS = 0.0
        var totalDistance = 0.0
        val points = mutableListOf(PointElevation(startPoint, startElevation, Double.NaN, "Referência"))

        stations.forEachIndexed { index, station ->
            validateReading(station.backsight, "Ré")
            validateReading(station.foresight, "Vante")
            require(station.distanceMeters >= 0.0 && station.distanceMeters.isFinite()) { "Distância inválida na estação ${index + 1}." }
            require(station.backsightPoint.isNotBlank() && station.foresightPoint.isNotBlank()) { "Identifique os pontos de ré e vante." }

            val hi = currentElevation + station.backsight
            sumBS += station.backsight
            points += PointElevation(station.backsightPoint, currentElevation, hi, "Ré")

            station.intermediates.forEach { sight ->
                validateReading(sight.reading, "Intermediária")
                require(sight.point.isNotBlank()) { "Identifique o ponto intermediário." }
                points += PointElevation(sight.point, hi - sight.reading, hi, "Intermediária")
            }

            currentElevation = hi - station.foresight
            sumFS += station.foresight
            totalDistance += station.distanceMeters
            points += PointElevation(station.foresightPoint, currentElevation, hi, "Vante")
        }

        return Result(
            points = points,
            finalElevation = currentElevation,
            totalDistanceMeters = totalDistance,
            sumBacksights = sumBS,
            sumForesights = sumFS,
            arithmeticCheck = startElevation + sumBS - sumFS
        )
    }

    /** Ajusta as cotas dos pontos proporcionalmente à distância acumulada.
     * closingError = cota calculada no fim - cota conhecida no fim.
     */
    fun compensate(
        elevations: List<Double>,
        cumulativeDistances: List<Double>,
        closingError: Double
    ): List<Double> {
        require(elevations.isNotEmpty() && elevations.size == cumulativeDistances.size) {
            "Informe uma cota e uma distância acumulada para cada ponto."
        }
        require(closingError.isFinite()) { "Erro de fechamento inválido." }
        require(cumulativeDistances.all { it >= 0.0 && it.isFinite() }) { "Distâncias acumuladas inválidas." }
        require(cumulativeDistances.first() == 0.0) { "A distância acumulada do ponto inicial deve ser zero." }
        val total = cumulativeDistances.last()
        require(total > 0.0) { "A distância total deve ser positiva." }
        require(cumulativeDistances.zipWithNext().all { (a, b) -> b >= a }) { "As distâncias devem estar em ordem crescente." }
        return elevations.mapIndexed { i, elevation ->
            require(elevation.isFinite()) { "Cota inválida." }
            elevation - closingError * cumulativeDistances[i] / total
        }
    }

    private fun validateReading(value: Double, label: String) {
        require(value >= 0.0 && value.isFinite()) { "Leitura de $label inválida." }
    }
}
