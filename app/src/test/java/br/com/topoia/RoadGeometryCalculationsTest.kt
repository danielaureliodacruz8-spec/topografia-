package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class RoadGeometryCalculationsTest {
    @Test
    fun calculatesSimpleHorizontalCurve() {
        val c = RoadGeometryCalculations.horizontalCurve(200.0, 60.0, 1000.0)
        assertEquals(115.4700538, c.tangent, 1e-6)
        assertEquals(209.4395102, c.development, 1e-6)
        assertEquals(884.5299462, c.pcStation, 1e-6)
        assertEquals(1093.9694564, c.ptStation, 1e-6)
    }

    @Test
    fun calculatesSymmetricVerticalCurveAndIntermediateElevation() {
        val c = RoadGeometryCalculations.verticalCurve(
            length = 100.0, incomingGradePercent = 2.0, outgoingGradePercent = -2.0,
            pviStation = 1000.0, pviElevation = 500.0
        )
        assertEquals(950.0, c.pvcStation, 1e-9)
        assertEquals(1050.0, c.pvtStation, 1e-9)
        assertEquals(499.0, c.pvcElevation, 1e-9)
        assertEquals(499.0, c.pvtElevation, 1e-9)
        assertEquals(25.0, c.kValue, 1e-9)
        assertEquals(499.5, RoadGeometryCalculations.verticalElevation(c, 50.0), 1e-9)
    }

    @Test
    fun stationingUsesTwentyMeterIntervalByDefault() {
        assertEquals("725+0.000", RoadGeometryCalculations.stationLabel(14500.0))
        assertEquals("725+5.500", RoadGeometryCalculations.stationLabel(14505.5))
    }

    @Test
    fun rejectsInvalidHorizontalCurve() {
        assertThrows(IllegalArgumentException::class.java) {
            RoadGeometryCalculations.horizontalCurve(0.0, 60.0, 100.0)
        }
    }
}
