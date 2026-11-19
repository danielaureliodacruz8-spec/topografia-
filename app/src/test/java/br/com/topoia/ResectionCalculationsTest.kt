package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.math.hypot

class ResectionCalculationsTest {
    @Test fun recoversUnknownStationFromFourControls() {
        val expectedE = 120.0
        val expectedN = 85.0
        val controls = listOf(
            0.0 to 0.0,
            300.0 to 0.0,
            0.0 to 250.0,
            300.0 to 250.0
        )
        val observations = controls.mapIndexed { i, p ->
            ResectionCalculations.DistanceObservation(
                "A${i + 1}", p.first, p.second,
                hypot(expectedE - p.first, expectedN - p.second)
            )
        }
        val result = ResectionCalculations.byDistances(observations, 130.0, 90.0)
        assertTrue(result.converged)
        assertEquals(expectedE, result.east, 1e-6)
        assertEquals(expectedN, result.north, 1e-6)
        assertEquals(0.0, result.rmsResidual, 1e-6)
    }

    @Test fun rejectsInsufficientControls() {
        assertThrows(IllegalArgumentException::class.java) {
            ResectionCalculations.byDistances(
                listOf(
                    ResectionCalculations.DistanceObservation("A", 0.0, 0.0, 10.0),
                    ResectionCalculations.DistanceObservation("B", 10.0, 0.0, 10.0)
                ), 5.0, 5.0
            )
        }
    }

    @Test fun rejectsCollinearControlGeometry() {
        assertThrows(IllegalArgumentException::class.java) {
            ResectionCalculations.byDistances(
                listOf(
                    ResectionCalculations.DistanceObservation("A", 0.0, 0.0, 10.0),
                    ResectionCalculations.DistanceObservation("B", 20.0, 0.0, 10.0),
                    ResectionCalculations.DistanceObservation("C", 40.0, 0.0, 30.0)
                ), 15.0, 5.0
            )
        }
    }
}
