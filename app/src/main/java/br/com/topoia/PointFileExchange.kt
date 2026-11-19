package br.com.topoia

import java.util.Locale

object PointFileExchange {
    data class Point(val name: String, val east: Double, val north: Double, val elevation: Double? = null)

    fun parse(text: String): List<Point> {
        val lines = text.lineSequence().map { it.trimEnd('\r') }.filter { it.isNotBlank() }.toList()
        require(lines.size >= 2) { "Arquivo precisa conter cabeçalho e pelo menos um ponto." }
        val delimiter = detectDelimiter(lines.first())
        val header = splitLine(lines.first(), delimiter).map { normalizeHeader(it) }
        val nameIndex = header.indexOfFirst { it in setOf("name", "nome", "point", "ponto", "id") }
        val eastIndex = header.indexOfFirst { it in setOf("e", "east", "easting", "este", "x") }
        val northIndex = header.indexOfFirst { it in setOf("n", "north", "northing", "norte", "y") }
        val elevationIndex = header.indexOfFirst { it in setOf("z", "h", "elevation", "cota", "altitude", "elevacao") }
        require(eastIndex >= 0 && northIndex >= 0) { "Cabeçalho deve identificar Este/E e Norte/N." }
        val decimalComma = delimiter != ','
        val points = lines.drop(1).mapIndexed { index, line ->
            val fields = splitLine(line, delimiter)
            fun field(i: Int): String = fields.getOrNull(i)?.trim().orEmpty()
            val name = if (nameIndex >= 0) field(nameIndex).ifBlank { "P\${index + 1}" } else "P\${index + 1}"
            val east = parseNumber(field(eastIndex), decimalComma)
            val north = parseNumber(field(northIndex), decimalComma)
            val elevation = if (elevationIndex >= 0 && field(elevationIndex).isNotBlank())
                parseNumber(field(elevationIndex), decimalComma) else null
            Point(name, east, north, elevation)
        }
        require(points.map { it.name }.distinct().size == points.size) { "Existem nomes de pontos repetidos." }
        return points
    }

    fun exportCsv(points: List<Point>): String {
        require(points.isNotEmpty()) { "Não há pontos para exportar." }
        require(points.all { it.name.isNotBlank() && it.east.isFinite() && it.north.isFinite() &&
            (it.elevation == null || it.elevation.isFinite()) }) { "Há pontos com dados inválidos." }
        val hasElevation = points.any { it.elevation != null }
        return buildString {
            append(if (hasElevation) "name,east,north,elevation\n" else "name,east,north\n")
            points.forEach { point ->
                append(escape(point.name)).append(',').append(format(point.east)).append(',').append(format(point.north))
                if (hasElevation) append(',').append(point.elevation?.let(::format).orEmpty())
                append('\n')
            }
        }
    }

    private fun detectDelimiter(header: String): Char =
        listOf(',', ';', '\t').maxByOrNull { splitLine(header, it).size } ?: ','

    private fun splitLine(line: String, delimiter: Char): List<String> {
        val fields = mutableListOf<String>()
        val current = StringBuilder()
        var quoted = false
        var i = 0
        while (i < line.length) {
            val ch = line[i]
            when {
                ch == '"' && quoted && i + 1 < line.length && line[i + 1] == '"' -> { current.append('"'); i++ }
                ch == '"' -> quoted = !quoted
                ch == delimiter && !quoted -> { fields += current.toString(); current.setLength(0) }
                else -> current.append(ch)
            }
            i++
        }
        require(!quoted) { "Campo com aspas não foi fechado." }
        fields += current.toString()
        return fields
    }

    private fun normalizeHeader(value: String): String = value.trim().lowercase(Locale.ROOT)
        .replace("á", "a").replace("ã", "a").replace("é", "e").replace("ê", "e")
        .replace("í", "i").replace("ó", "o").replace("ô", "o").replace("õ", "o")
        .replace("ú", "u").replace("ç", "c")

    private fun parseNumber(value: String, decimalComma: Boolean): Double {
        val normalized = if (decimalComma) value.replace(".", "").replace(",", ".") else value
        return normalized.toDoubleOrNull()?.takeIf { it.isFinite() }
            ?: throw IllegalArgumentException("Número inválido no arquivo: '$value'.")
    }

    private fun escape(value: String): String =
        if (value.contains(',') || value.contains('"') || value.contains('\n'))
            "\"\${value.replace("\"", "\"\"")}\"" else value

    private fun format(value: Double): String = String.format(Locale.US, "%.4f", value)
}
