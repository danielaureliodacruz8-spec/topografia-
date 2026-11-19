package br.com.topoia

import org.junit.Assert.*
import org.junit.Test

class TopoCalculationsTest {
    @Test fun inverseNorthEast() {
        val r = TopoCalculations.inverse(500000.0, 8000000.0, 500030.0, 8000040.0)
        assertEquals(50.0, r.distance, 1e-9)
        assertEquals(36.8698976458, r.azimuthDegrees, 1e-8)
    }

    @Test fun levelingKnownExample() {
        val r = TopoCalculations.leveling(100.0, 1.455, 2.130)
        assertEquals(101.455, r.instrumentHeight, 1e-9)
        assertEquals(99.325, r.pointElevation, 1e-9)
    }

    @Test fun forwardThenInverseReturnsDistance() {
        val target = TopoCalculations.forward(500000.0, 8000000.0, 100.0, 90.0)
        val inverse = TopoCalculations.inverse(500000.0, 8000000.0, target.first, target.second)
        assertEquals(100.0, inverse.distance, 1e-8)
        assertEquals(90.0, inverse.azimuthDegrees, 1e-8)
    }

    @Test fun stationAt14500With20mInterval() {
        val (station, offset) = TopoCalculations.stationNumber(14500.0, 20.0)
        assertEquals(725, station)
        assertEquals(0.0, offset, 1e-9)
    }

    @Test fun polygonAreaSquare() {
        val r = GeometryCalculations.polygonArea(listOf(
            0.0 to 0.0, 10.0 to 0.0, 10.0 to 10.0, 0.0 to 10.0
        ))
        assertEquals(100.0, r.area, 1e-9)
        assertEquals(40.0, r.perimeter, 1e-9)
    }

    @Test fun slopeFivePercent() {
        val r = GeometryCalculations.slope(100.0, 5.0)
        assertEquals(5.0, r.slopePercent, 1e-9)
    }

    @Test fun inclinedDistanceReduction() {
        val r = GeometryCalculations.reduceSlopeDistance(100.0, 30.0)
        assertEquals(86.6025403784, r.horizontal, 1e-8)
        assertEquals(50.0, r.vertical, 1e-8)
    }

    @Test fun bowditchSquareCloses() {
        val start = PoligonalCalculations.Coordinate("A", 0.0, 0.0)
        val sides = listOf(
            PoligonalCalculations.Side("B", 100.0, 90.0),
            PoligonalCalculations.Side("C", 100.0, 0.0),
            PoligonalCalculations.Side("D", 100.0, 270.0),
            PoligonalCalculations.Side("A", 100.0, 180.0)
        )
        val r = PoligonalCalculations.bowditch(start, sides)
        assertEquals(0.0, r.linearMisclosure, 1e-8)
        assertEquals(400.0, r.sumDistances, 1e-8)
    }
}
