package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class EarthworkCalculationsTest {
    @Test
    fun computesCutAndFillWithZeroCrossing() {
        val areas = EarthworkCalculations.sectionAreas(
            offsets = listOf(-5.0, 0.0, 5.0),
            designMinusGround = listOf(-2.0, 2.0, -2.0)
        )
        assertEquals(5.0, areas.cutArea, 1e-9)
        assertEquals(5.0, areas.fillArea, 1e-9)
    }

    @Test
    fun computesAverageEndAreaVolumes() {
        val a = EarthworkCalculations.Section(0.0, EarthworkCalculations.SectionAreas(10.0, 4.0))
        val b = EarthworkCalculations.Section(20.0, EarthworkCalculations.SectionAreas(14.0, 8.0))
        val v = EarthworkCalculations.averageEndArea(a, b)
        assertEquals(240.0, v.cutVolume, 1e-9)
        assertEquals(120.0, v.fillVolume, 1e-9)
    }

    @Test
    fun computesPrismoidalVolume() {
        val a = EarthworkCalculations.Section(0.0, EarthworkCalculations.SectionAreas(10.0, 2.0))
        val m = EarthworkCalculations.Section(10.0, EarthworkCalculations.SectionAreas(14.0, 4.0))
        val b = EarthworkCalculations.Section(20.0, EarthworkCalculations.SectionAreas(12.0, 6.0))
        val v = EarthworkCalculations.prismoidal(a, m, b)
        assertEquals(273.333333333, v.cutVolume, 1e-8)
        assertEquals(80.0, v.fillVolume, 1e-9)
    }

    @Test
    fun accumulatesCutAndFillSeparately() {
        val sections = listOf(
            EarthworkCalculations.Section(0.0, EarthworkCalculations.SectionAreas(10.0, 2.0)),
            EarthworkCalculations.Section(20.0, EarthworkCalculations.SectionAreas(14.0, 4.0)),
            EarthworkCalculations.Section(40.0, EarthworkCalculations.SectionAreas(12.0, 6.0))
        )
        val result = EarthworkCalculations.cumulative(sections)
        assertEquals(480.0, result.cumulativeCut.last(), 1e-9)
        assertEquals(120.0, result.cumulativeFill.last(), 1e-9)
    }

    @Test
    fun rejectsUnorderedOffsets() {
        assertThrows(IllegalArgumentException::class.java) {
            EarthworkCalculations.sectionAreas(listOf(0.0, -1.0), listOf(1.0, 2.0))
        }
    }
}
