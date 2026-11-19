package br.com.topoia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val Navy = Color(0xFF0B1220)
private val Panel = Color(0xFF172338)
private val Cyan = Color(0xFF38BDF8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TopoIAApp() }
    }
}

@Composable
fun TopoIAApp() {
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Cyan,
        background = Navy,
        surface = Panel,
        onPrimary = Navy,
        onBackground = Color.White,
        onSurface = Color.White
    )) {
        Surface(modifier = Modifier.fillMaxSize(), color = Navy) {
            Dashboard()
        }
    }
}

@Composable
private fun Dashboard() {
    var selected by remember { mutableStateOf("Início") }
    val modules = listOf("Início", "Coordenadas", "Nivelamento", "Estacas")
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("TOPO IA", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Cyan)
        Text("Central inteligente de topografia", color = Color.LightGray)
        Spacer(Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Panel), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("MODO TOPOGRAFIA", color = Cyan, fontWeight = FontWeight.Bold)
                Text("Cálculos técnicos offline • Versão 0.1.0", color = Color.White)
                Text("Motor matemático inicial pronto para expansão.", color = Color.LightGray, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            modules.forEach { item ->
                Button(onClick = { selected = item }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(item, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        when (selected) {
            "Coordenadas" -> InverseCalculator()
            "Nivelamento" -> LevelCalculator()
            "Estacas" -> StationCalculator()
            else -> HomeModules()
        }
    }
}

@Composable
private fun HomeModules() {
    Text("Módulos do projeto", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    val items = listOf(
        "Poligonais e ajustamento" to "Ângulos, azimutes, distâncias, fechamento e coordenadas.",
        "Coordenadas" to "Cálculo inverso e irradiação de pontos.",
        "Nivelamento" to "Ré, vante, altura do instrumento, cotas e desníveis.",
        "Geometria rodoviária" to "Estaqueamento, curvas, greide, rampas e superelevação.",
        "Terraplenagem e drenagem" to "Corte, aterro, volumes, taludes e dispositivos.",
        "Modo Estrada + IA" to "Leitura de nota de serviço, coletora e orientação de locação."
    )
    items.forEach { (title, description) ->
        Card(Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(containerColor = Panel)) {
            Column(Modifier.padding(15.dp)) {
                Text(title, color = Cyan, fontWeight = FontWeight.SemiBold)
                Text(description, color = Color.LightGray, fontSize = 13.sp)
            }
        }
    }
    Text("Os módulos listados são o plano de evolução; somente as calculadoras identificadas como disponíveis estão implementadas nesta versão.", color = Color.Yellow, fontSize = 12.sp)
}

@Composable
private fun NumberField(label: String, value: String, onValue: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValue, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
}

private fun parse(value: String): Double? = value.trim().replace(",", ".").toDoubleOrNull()
private fun fmt(value: Double): String = String.format(Locale("pt", "BR"), "%.4f", value)

@Composable
private fun InverseCalculator() {
    var e1 by remember { mutableStateOf("500000") }
    var n1 by remember { mutableStateOf("8000000") }
    var e2 by remember { mutableStateOf("500030") }
    var n2 by remember { mutableStateOf("8000040") }
    var result by remember { mutableStateOf<String?>(null) }
    Text("Cálculo inverso: distância e azimute", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    NumberField("Este do ponto 1 (E)", e1) { e1 = it }
    NumberField("Norte do ponto 1 (N)", n1) { n1 = it }
    NumberField("Este do ponto 2 (E)", e2) { e2 = it }
    NumberField("Norte do ponto 2 (N)", n2) { n2 = it }
    Button(onClick = {
        val values = listOf(e1, n1, e2, n2).mapNotNull(::parse)
        result = if (values.size == 4) {
            val r = TopoCalculations.inverse(values[0], values[1], values[2], values[3])
            "Distância horizontal: ${fmt(r.distance)} m\nAzimute: ${fmt(r.azimuthDegrees)}°"
        } else "Confira os quatro valores informados."
    }, modifier = Modifier.fillMaxWidth()) { Text("CALCULAR") }
    result?.let { ResultCard(it) }
}

@Composable
private fun LevelCalculator() {
    var known by remember { mutableStateOf("100.000") }
    var back by remember { mutableStateOf("1.455") }
    var fore by remember { mutableStateOf("2.130") }
    var result by remember { mutableStateOf<String?>(null) }
    Text("Nivelamento geométrico simples", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    NumberField("Cota conhecida / RN (m)", known) { known = it }
    NumberField("Leitura de ré (m)", back) { back = it }
    NumberField("Leitura de vante (m)", fore) { fore = it }
    Button(onClick = {
        val a = parse(known); val b = parse(back); val c = parse(fore)
        result = if (a != null && b != null && c != null) {
            val r = TopoCalculations.leveling(a, b, c)
            "Altura do instrumento: ${fmt(r.instrumentHeight)} m\nCota do ponto: ${fmt(r.pointElevation)} m\nDesnível: ${fmt(r.difference)} m"
        } else "Confira as leituras informadas."
    }, modifier = Modifier.fillMaxWidth()) { Text("CALCULAR COTA") }
    result?.let { ResultCard(it) }
}

@Composable
private fun StationCalculator() {
    var chainage by remember { mutableStateOf("14500") }
    var interval by remember { mutableStateOf("20") }
    var result by remember { mutableStateOf<String?>(null) }
    Text("Conversor de estacas", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    NumberField("Distância acumulada (m)", chainage) { chainage = it }
    NumberField("Intervalo entre estacas (m)", interval) { interval = it }
    Button(onClick = {
        val d = parse(chainage); val i = parse(interval)
        result = if (d != null && i != null && i > 0 && d >= 0) {
            val (number, remainder) = TopoCalculations.stationNumber(d, i)
            "Estaca: $number + ${fmt(remainder)} m"
        } else "Informe distância válida e intervalo maior que zero."
    }, modifier = Modifier.fillMaxWidth()) { Text("IDENTIFICAR ESTACA") }
    result?.let { ResultCard(it) }
}

@Composable
private fun ResultCard(text: String) {
    Card(Modifier.fillMaxWidth().padding(top = 14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF12352F))) {
        Text(text, Modifier.padding(16.dp), color = Color(0xFF86EFAC), fontWeight = FontWeight.SemiBold)
    }
}
