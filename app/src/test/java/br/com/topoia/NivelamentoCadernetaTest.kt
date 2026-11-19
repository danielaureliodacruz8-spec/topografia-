package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NivelamentoCadernetaTest {
    @Test
    fun calculatesBacksightIntermediateAndForesight() {
        val result = NivelamentoCaderneta.calculate(
            startPoint = "RN-01",
            startElevation = 100.0,
            stations = listOf(
                NivelamentoCaderneta.Station(
                    backsightPoint = "RN-01",
                    backsight = 1.5,
                    intermediates = listOf(NivelamentoCaderneta.IntermediateSight("P-01", 2.0)),
                    foresightPoint = "PM-01",
                    foresight = 2.2,
                    distanceMeters = 100.0
                ),
                NivelamentoCaderneta.Station(
                    backsightPoint = "PM-01",
                    backsight = 1.1,
                    foresightPoint = "P-02",
                    foresight = 0.9,
                    distanceMeters = 80.0
                )
            )
        )

        assertEquals(101.5, result.points.first { it.point == "P-01" }.elevation, 1e-9)
        assertEquals(99.3, result.points.first { it.point == "PM-01" && it.type == "Vante" }.elevation, 1e-9)
        assertEquals(99.5, result.finalElevation, 1e-9)
        assertEquals(180.0, result.totalDistanceMeters, 1e-9)
        assertEquals(result.finalElevation, result.arithmeticCheck, 1e-9)
    }

    @Test
    fun distributesClosingErrorByDistance() {
        val adjusted = NivelamentoCaderneta.compensate(
            elevations = listOf(100.0, 99.8, 99.5),
            cumulativeDistances = listOf(0.0, 50.0, 100.0),
            closingError = 0.10
        )
        assertEquals(100.0, adjusted[0], 1e-9)
        assertEquals(99.75, adjusted[1], 1e-9)
        assertEquals(99.4, adjusted[2], 1e-9)
    }

    @Test
    fun rejectsInvalidDistanceOrder() {
        assertThrows(IllegalArgumentException::class.java) {
            NivelamentoCaderneta.compensate(
                elevations = listOf(10.0, 9.0, 8.0),
                cumulativeDistances = listOf(0.0, 20.0, 10.0),
                closingError = 0.0
            )
        }
    }
}
