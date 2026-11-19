package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class RoadSectionCalculationsTest {
    @Test
    fun calculatesPlatformEdgeWithCrossfall() {
        val left = RoadSectionCalculations.platformEdge(100.0, 7.0, 3.0, -1)
        val right = RoadSectionCalculations.platformEdge(100.0, 7.0, 3.0, 1)
        assertEquals(-7.0, left.offset, 1e-9)
        assertEquals(99.79, left.elevation, 1e-9)
        assertEquals(7.0, right.offset, 1e-9)
        assertEquals(99.79, right.elevation, 1e-9)
    }

    @Test
    fun findsFillDaylightByLinearInterpolation() {
        val edge = RoadSectionCalculations.Edge(7.0, 100.0)
        val result = RoadSectionCalculations.daylight(
            edge,
            listOf(
                RoadSectionCalculations.GroundPoint(7.0, 98.0),
                RoadSectionCalculations.GroundPoint(10.0, 99.0),
                RoadSectionCalculations.GroundPoint(13.0, 101.0)
            ),
            1.5,
            1
        )
        assertNotNull(result)
        assertEquals("Aterro", result!!.type)
        assertEquals(10.5, result.offset, 1e-9)
        assertEquals(99.0, result.elevation, 1e-9)
    }

    @Test
    fun findsCutDaylight() {
        val result = RoadSectionCalculations.daylight(
            RoadSectionCalculations.Edge(-5.0, 100.0),
            listOf(
                RoadSectionCalculations.GroundPoint(5.0, 102.0),
                RoadSectionCalculations.GroundPoint(8.0, 100.0),
                RoadSectionCalculations.GroundPoint(11.0, 98.0)
            ),
            1.5,
            -1
        )
        assertNotNull(result)
        assertEquals("Corte", result!!.type)
        assertEquals(-8.0, result.offset, 1e-9)
    }

    @Test
    fun returnsNullWhenTerrainDoesNotMeetSlope() {
        val result = RoadSectionCalculations.daylight(
            RoadSectionCalculations.Edge(5.0, 100.0),
            listOf(
                RoadSectionCalculations.GroundPoint(5.0, 98.0),
                RoadSectionCalculations.GroundPoint(8.0, 98.5)
            ),
            1.5,
            1
        )
        assertNull(result)
    }

    @Test
    fun rejectsInvalidTerrainOrder() {
        assertThrows(IllegalArgumentException::class.java) {
            RoadSectionCalculations.daylight(
                RoadSectionCalculations.Edge(5.0, 100.0),
                listOf(
                    RoadSectionCalculations.GroundPoint(5.0, 99.0),
                    RoadSectionCalculations.GroundPoint(4.0, 98.0)
                ),
                1.5,
                1
            )
        }
    }
}
