package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PoligonalCalculationsTest {
    @Test
    fun adjustsSquareTraverseFromInteriorAngles() {
        val result = PoligonalCalculations.closedFromInteriorAngles(
            start = PoligonalCalculations.Coordinate("P0", 500.0, 1000.0),
            distances = listOf(100.0, 100.0, 100.0, 100.0),
            interiorAngles = listOf(90.0, 90.0, 90.0, 90.0),
            initialAzimuthDegrees = 0.0,
            clockwise = true
        )

        assertEquals(0.0, result.angularMisclosureDegrees, 1e-9)
        assertEquals(0.0, result.azimuthsDegrees[0], 1e-9)
        assertEquals(90.0, result.azimuthsDegrees[1], 1e-9)
        assertEquals(180.0, result.azimuthsDegrees[2], 1e-9)
        assertEquals(270.0, result.azimuthsDegrees[3], 1e-9)
        assertEquals(500.0, result.coordinateResult.coordinates.last().easting, 1e-8)
        assertEquals(1000.0, result.coordinateResult.coordinates.last().northing, 1e-8)
    }

    @Test
    fun distributesAngularMisclosureEqually() {
        val result = PoligonalCalculations.closedFromInteriorAngles(
            start = PoligonalCalculations.Coordinate("P0", 0.0, 0.0),
            distances = listOf(50.0, 50.0, 50.0, 50.0),
            interiorAngles = listOf(90.1, 89.9, 90.0, 90.0),
            initialAzimuthDegrees = 0.0
        )
        assertEquals(0.0, result.angularMisclosureDegrees, 1e-9)
        assertEquals(90.0, result.adjustedAnglesDegrees[0], 1e-9)
    }

    @Test
    fun rejectsWrongNumberOfAngles() {
        assertThrows(IllegalArgumentException::class.java) {
            PoligonalCalculations.closedFromInteriorAngles(
                start = PoligonalCalculations.Coordinate("P0", 0.0, 0.0),
                distances = listOf(10.0, 10.0, 10.0),
                interiorAngles = listOf(90.0, 90.0),
                initialAzimuthDegrees = 0.0
            )
        }
    }
}
