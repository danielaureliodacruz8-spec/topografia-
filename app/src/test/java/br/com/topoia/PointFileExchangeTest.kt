package br.com.topoia

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class PointFileExchangeTest {
    @Test fun importsBrazilianSemicolonFileWithDecimalComma() {
        val data = "Nome;Este;Norte;Cota\nP1;500.123,45;8.234.567,89;712,35"
        val points = PointFileExchange.parse(data)
        assertEquals(1, points.size)
        assertEquals("P1", points[0].name)
        assertEquals(500123.45, points[0].east, 1e-8)
        assertEquals(8234567.89, points[0].north, 1e-8)
        assertEquals(712.35, points[0].elevation!!, 1e-8)
    }

    @Test fun exportsAndImportsCommaCsv() {
        val original = listOf(
            PointFileExchange.Point("P,01", 100.1234, 200.5678, 9.25),
            PointFileExchange.Point("P2", 110.0, 220.0, null)
        )
        val parsed = PointFileExchange.parse(PointFileExchange.exportCsv(original))
        assertEquals(original.size, parsed.size)
        assertEquals(original[0].name, parsed[0].name)
        assertEquals(original[0].east, parsed[0].east, 1e-9)
        assertEquals(original[1].north, parsed[1].north, 1e-9)
        assertNull(parsed[1].elevation)
    }

    @Test fun rejectsMissingCoordinateColumns() {
        assertThrows(IllegalArgumentException::class.java) {
            PointFileExchange.parse("name,cota\nP1,10")
        }
    }
}
