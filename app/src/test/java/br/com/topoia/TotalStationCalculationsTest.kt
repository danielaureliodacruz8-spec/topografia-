package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TotalStationCalculationsTest {
    @Test fun radiatesNorthAndEast() {
        val station = TotalStationCalculations.Coordinate(100.0, 200.0, 50.0)
        val north = TotalStationCalculations.radiate(station, 10.0, 0.0)
        val east = TotalStationCalculations.radiate(station, 10.0, 90.0)
        assertEquals(100.0, north.east, 1e-9)
        assertEquals(210.0, north.north, 1e-9)
        assertEquals(110.0, east.east, 1e-9)
        assertEquals(200.0, east.north, 1e-9)
    }

    @Test fun reducesZenithObservationAndHeights() {
        val result = TotalStationCalculations.reduceZenithObservation(
            TotalStationCalculations.Coordinate(0.0, 0.0, 100.0),
            10.0, 60.0, 1.5, 2.0, 0.0, 0.0
        )
        assertEquals(8.6602540378, result.horizontalDistance, 1e-8)
        assertEquals(5.0, result.verticalDifference, 1e-9)
        assertEquals(104.5, result.target.elevation!!, 1e-9)
        assertEquals(8.6602540378, result.target.north, 1e-8)
    }

    @Test fun stakeoutReturnsDistanceAndAzimuth() {
        val result = TotalStationCalculations.stakeout(
            TotalStationCalculations.Coordinate(0.0, 0.0),
            TotalStationCalculations.Coordinate(3.0, 4.0)
        )
        assertEquals(5.0, result.distance, 1e-9)
        assertEquals(36.8698976458, result.azimuthDegrees, 1e-8)
    }

    @Test fun intersectsTwoForwardRays() {
        val result = TotalStationCalculations.intersectRays(
            TotalStationCalculations.Coordinate(0.0, 0.0), 90.0,
            TotalStationCalculations.Coordinate(10.0, 0.0), 0.0
        )
        assertEquals(10.0, result.point.east, 1e-9)
        assertEquals(0.0, result.point.north, 1e-9)
        assertEquals(10.0, result.distanceFromFirst, 1e-9)
        assertEquals(0.0, result.distanceFromSecond, 1e-9)
    }

    @Test fun rejectsParallelSightLines() {
        assertThrows(IllegalArgumentException::class.java) {
            TotalStationCalculations.intersectRays(
                TotalStationCalculations.Coordinate(0.0, 0.0), 90.0,
                TotalStationCalculations.Coordinate(0.0, 10.0), 90.0
            )
        }
    }
}
