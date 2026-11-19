package br.com.topoia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
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
    val modules = listOf("Início", "Coordenadas", "Nivelamento", "Caderneta", "Estacas", "Estação total", "Resseção", "Rodovia", "Arquivos")
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
            "Caderneta" -> LevelBookCalculator()
            "Estacas" -> StationCalculator()
            "Estação total" -> TotalStationCalculator()
            "Resseção" -> ResectionCalculator()
            "Rodovia" -> RoadGeometryCalculator()
            "Arquivos" -> PointFileScreen()
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
        "Estação total" to "Irradiação por azimute e distância horizontal; redução e locação em expansão.",
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

@Composable
private fun TotalStationCalculator() {
    var east by remember { mutableStateOf("500000") }
    var north by remember { mutableStateOf("8000000") }
    var distance by remember { mutableStateOf("25") }
    var azimuth by remember { mutableStateOf("90") }
    var result by remember { mutableStateOf<String?>(null) }
    Text("Irradiação por estação total", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    Text("Informe a coordenada da estação, a distância horizontal e o azimute contado do Norte no sentido horário.", color = Color.LightGray, fontSize = 12.sp)
    NumberField("Este da estação (E)", east) { east = it }
    NumberField("Norte da estação (N)", north) { north = it }
    NumberField("Distância horizontal (m)", distance) { distance = it }
    NumberField("Azimute (graus)", azimuth) { azimuth = it }
    Button(onClick = {
        val e = parse(east); val n = parse(north); val d = parse(distance); val a = parse(azimuth)
        result = if (e != null && n != null && d != null && a != null && d >= 0) {
            val point = TotalStationCalculations.radiate(TotalStationCalculations.Coordinate(e, n), d, a)
            "Ponto calculado\nE: ${fmt(point.east)} m\nN: ${fmt(point.north)} m\nDistância: ${fmt(d)} m\nAzimute: ${fmt(((a % 360.0) + 360.0) % 360.0)}°"
        } else "Confira os valores. A distância não pode ser negativa."
    }, modifier = Modifier.fillMaxWidth()) { Text("CALCULAR PONTO") }
    result?.let { ResultCard(it) }
}

@Composable
private fun ResectionCalculator() {
    var e1 by remember { mutableStateOf("0") }
    var n1 by remember { mutableStateOf("0") }
    var d1 by remember { mutableStateOf("50") }
    var e2 by remember { mutableStateOf("100") }
    var n2 by remember { mutableStateOf("0") }
    var d2 by remember { mutableStateOf("78.1025") }
    var e3 by remember { mutableStateOf("0") }
    var n3 by remember { mutableStateOf("100") }
    var d3 by remember { mutableStateOf("80.6226") }
    var initialE by remember { mutableStateOf("35") }
    var initialN by remember { mutableStateOf("25") }
    var result by remember { mutableStateOf<String?>(null) }
    Text("Resseção por distâncias", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    Text("Informe três ou mais pontos de apoio conhecidos e as distâncias medidas da estação desconhecida. Esta tela usa três apoios.", color = Color.LightGray, fontSize = 12.sp)
    Text("Apoio 1", color = Cyan, fontWeight = FontWeight.Bold)
    NumberField("E do apoio 1", e1) { e1 = it }
    NumberField("N do apoio 1", n1) { n1 = it }
    NumberField("Distância medida até apoio 1 (m)", d1) { d1 = it }
    Text("Apoio 2", color = Cyan, fontWeight = FontWeight.Bold)
    NumberField("E do apoio 2", e2) { e2 = it }
    NumberField("N do apoio 2", n2) { n2 = it }
    NumberField("Distância medida até apoio 2 (m)", d2) { d2 = it }
    Text("Apoio 3", color = Cyan, fontWeight = FontWeight.Bold)
    NumberField("E do apoio 3", e3) { e3 = it }
    NumberField("N do apoio 3", n3) { n3 = it }
    NumberField("Distância medida até apoio 3 (m)", d3) { d3 = it }
    Text("Estimativa inicial da estação", color = Cyan, fontWeight = FontWeight.Bold)
    NumberField("E aproximado", initialE) { initialE = it }
    NumberField("N aproximado", initialN) { initialN = it }
    Button(onClick = {
        val v = listOf(e1,n1,d1,e2,n2,d2,e3,n3,d3,initialE,initialN).map { parse(it) }
        result = if (v.all { it != null }) {
            runCatching {
                val a = v.map { it!! }
                val r = ResectionCalculations.byDistances(
                    listOf(
                        ResectionCalculations.DistanceObservation("A1",a[0],a[1],a[2]),
                        ResectionCalculations.DistanceObservation("A2",a[3],a[4],a[5]),
                        ResectionCalculations.DistanceObservation("A3",a[6],a[7],a[8])
                    ), a[9], a[10]
                )
                if (!r.converged) "Não convergiu. Reveja as medidas e a estimativa inicial."
                else "Estação calculada\nE: ${fmt(r.east)} m\nN: ${fmt(r.north)} m\nRMS dos resíduos: ${fmt(r.rmsResidual)} m\nIterações: ${r.iterations}"
            }.getOrElse { "Não foi possível calcular: ${it.message}" }
        } else "Confira todos os valores numéricos."
    }, modifier = Modifier.fillMaxWidth()) { Text("CALCULAR RESSEÇÃO") }
    result?.let { ResultCard(it) }
}


@Composable
private fun PointFileScreen() {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("topo_ia_points", android.content.Context.MODE_PRIVATE) }
    val points = remember {
        mutableStateListOf<PointFileExchange.Point>().apply {
            val saved = preferences.getString("points_csv", null)
            if (!saved.isNullOrBlank()) runCatching { addAll(PointFileExchange.parse(saved)) }
        }
    }
    LaunchedEffect(points.toList()) {
        if (points.isEmpty()) preferences.edit().remove("points_csv").apply()
        else preferences.edit().putString("points_csv", PointFileExchange.exportCsv(points.toList())).apply()
    }
    var name by remember { mutableStateOf("P1") }
    var east by remember { mutableStateOf("") }
    var north by remember { mutableStateOf("") }
    var elevation by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Adicione pontos ou importe um arquivo CSV/TXT com cabeçalho.") }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            status = runCatching {
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Não foi possível abrir o arquivo.")
                val imported = PointFileExchange.parse(content)
                points.clear()
                points.addAll(imported)
                "Importados ${imported.size} pontos com sucesso."
            }.getOrElse { "Falha na importação: ${it.message}" }
        }
    }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            status = runCatching {
                val csv = PointFileExchange.exportCsv(points.toList())
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(csv) }
                    ?: error("Não foi possível gravar o arquivo.")
                "Arquivo CSV exportado com ${points.size} pontos."
            }.getOrElse { "Falha na exportação: ${it.message}" }
        }
    }

    Text("Cadastro e arquivos de pontos", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    Text("Coordenadas em metros. CSV/TXT com colunas Nome, Este/E, Norte/N e Cota/H.", color = Color.LightGray, fontSize = 12.sp)
    NumberField("Nome do ponto", name) { name = it }
    NumberField("Este (E)", east) { east = it }
    NumberField("Norte (N)", north) { north = it }
    NumberField("Cota / H (opcional)", elevation) { elevation = it }
    Button(onClick = {
        val e = parse(east); val n = parse(north)
        val h = if (elevation.isBlank()) null else parse(elevation)
        if (name.isBlank() || e == null || n == null || (elevation.isNotBlank() && h == null)) {
            status = "Confira nome e coordenadas. Use números válidos."
        } else if (points.any { it.name == name.trim() }) {
            status = "Já existe um ponto com esse nome."
        } else {
            points.add(PointFileExchange.Point(name.trim(), e, n, h))
            name = "P${points.size + 1}"; east = ""; north = ""; elevation = ""
            status = "Ponto adicionado."
        }
    }, modifier = Modifier.fillMaxWidth()) { Text("ADICIONAR PONTO") }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { importLauncher.launch(arrayOf("text/*", "application/*")) }) { Text("IMPORTAR") }
        Button(onClick = { exportLauncher.launch("pontos_topograficos.csv") }, enabled = points.isNotEmpty()) { Text("EXPORTAR CSV") }
    }
    Text(status, color = Cyan, fontSize = 13.sp)
    Text("Pontos cadastrados: ${points.size}", fontWeight = FontWeight.Bold)
    points.takeLast(20).forEach { point ->
        Text("${point.name} | E ${fmt(point.east)} | N ${fmt(point.north)}" +
            (point.elevation?.let { " | H ${fmt(it)}" } ?: ""), color = Color.LightGray, fontSize = 12.sp)
    }
    if (points.size > 20) Text("Exibindo os últimos 20 pontos.", color = Color.Gray, fontSize = 11.sp)
}


@Composable
private fun LevelBookCalculator() {
    var startPoint by remember { mutableStateOf("RN-01") }
    var startElevation by remember { mutableStateOf("100.000") }
    var rows by remember { mutableStateOf("RN-01;1.455;;PM-01;2.130;100") }
    var result by remember { mutableStateOf<String?>(null) }
    Text("Caderneta de nivelamento composto", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    Text("Uma estação por linha: ponto ré;leitura ré;intermediárias; ponto vante;leitura vante;distância. Intermediárias: P1:1.234|P2:1.456.", color = Color.LightGray, fontSize = 12.sp)
    NumberField("Ponto de referência", startPoint) { startPoint = it }
    NumberField("Cota conhecida do RN (m)", startElevation) { startElevation = it }
    OutlinedTextField(
        value = rows,
        onValueChange = { rows = it },
        label = { Text("Estações da caderneta") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 5
    )
    Button(onClick = {
        val elevation = parse(startElevation)
        result = if (elevation == null || startPoint.isBlank()) "Informe o RN e sua cota."
        else runCatching {
            val stations = rows.lineSequence().filter { it.isNotBlank() }.mapIndexed { index, line ->
                val f = line.split(';')
                require(f.size == 6) { "Linha ${index + 1}: informe seis campos separados por ponto e vírgula." }
                val intermediates = if (f[2].isBlank()) emptyList() else f[2].split('|').map { item ->
                    val pair = item.split(':')
                    require(pair.size == 2) { "Intermediária inválida na linha ${index + 1}." }
                    NivelamentoCaderneta.IntermediateSight(pair[0].trim(), parse(pair[1]) ?: error("Leitura intermediária inválida."))
                }
                NivelamentoCaderneta.Station(
                    f[0].trim(), parse(f[1]) ?: error("Leitura de ré inválida."),
                    intermediates, f[3].trim(), parse(f[4]) ?: error("Leitura de vante inválida."),
                    parse(f[5]) ?: error("Distância inválida.")
                )
            }.toList()
            val r = NivelamentoCaderneta.calculate(startPoint.trim(), elevation, stations)
            buildString {
                append("Cota final: ${fmt(r.finalElevation)} m\n")
                append("Σ ré: ${fmt(r.sumBacksights)} m | Σ vante: ${fmt(r.sumForesights)} m\n")
                append("Conferência: ${fmt(r.arithmeticCheck)} m\n")
                append("Distância acumulada: ${fmt(r.totalDistanceMeters)} m\n\n")
                r.points.drop(1).forEach { append("${it.point} (${it.type}): ${fmt(it.elevation)} m | HI: ${fmt(it.instrumentHeight)} m\n") }
            }
        }.getOrElse { "Erro na caderneta: ${it.message}" }
    }, modifier = Modifier.fillMaxWidth()) { Text("CALCULAR CADERNETA") }
    result?.let { ResultCard(it) }
}


@Composable
private fun RoadGeometryCalculator() {
    var radius by remember { mutableStateOf("500") }
    var delta by remember { mutableStateOf("30") }
    var piStation by remember { mutableStateOf("10000") }
    var horizontalResult by remember { mutableStateOf<String?>(null) }
    var length by remember { mutableStateOf("200") }
    var grade1 by remember { mutableStateOf("2") }
    var grade2 by remember { mutableStateOf("-3") }
    var pviStation by remember { mutableStateOf("12000") }
    var pviElevation by remember { mutableStateOf("700") }
    var verticalResult by remember { mutableStateOf<String?>(null) }

    Text("Geometria rodoviária", fontSize = 19.sp, fontWeight = FontWeight.Bold)
    Text("Curva circular horizontal", color = Cyan, fontWeight = FontWeight.Bold)
    NumberField("Raio (m)", radius) { radius = it }
    NumberField("Ângulo central Δ (graus)", delta) { delta = it }
    NumberField("Estaca acumulada do PI (m)", piStation) { piStation = it }
    Button(onClick = {
        val r = parse(radius); val a = parse(delta); val pi = parse(piStation)
        horizontalResult = if (r != null && a != null && pi != null) runCatching {
            val curve = RoadGeometryCalculations.horizontalCurve(r, a, pi)
            "Tangente: \${fmt(curve.tangent)} m\\nDesenvolvimento: \${fmt(curve.development)} m\\nCorda longa: \${fmt(curve.longChord)} m\\nExterna: \${fmt(curve.external)} m\\nOrdenada média: \${fmt(curve.middleOrdinate)} m\\nPC: \${fmt(curve.pcStation)} m (\${RoadGeometryCalculations.stationLabel(curve.pcStation)})\\nPT: \${fmt(curve.ptStation)} m (\${RoadGeometryCalculations.stationLabel(curve.ptStation)})"
        }.getOrElse { "Erro: \${it.message}" } else "Confira os dados da curva."
    }, modifier = Modifier.fillMaxWidth()) { Text("CALCULAR CURVA HORIZONTAL") }
    horizontalResult?.let { ResultCard(it) }

    Text("Curva vertical parabólica", color = Cyan, fontWeight = FontWeight.Bold)
    NumberField("Comprimento L (m)", length) { length = it }
    NumberField("Rampa de entrada g1 (%)", grade1) { grade1 = it }
    NumberField("Rampa de saída g2 (%)", grade2) { grade2 = it }
    NumberField("Estaca acumulada do PVI (m)", pviStation) { pviStation = it }
    NumberField("Cota do PVI (m)", pviElevation) { pviElevation = it }
    Button(onClick = {
        val values = listOf(length,grade1,grade2,pviStation,pviElevation).map { parse(it) }
        verticalResult = if (values.all { it != null }) runCatching {
            val v = values.map { it!! }
            val curve = RoadGeometryCalculations.verticalCurve(v[0],v[1],v[2],v[3],v[4])
            val mid = RoadGeometryCalculations.verticalElevation(curve, curve.length / 2.0)
            "PVC: \${fmt(curve.pvcStation)} m | cota \${fmt(curve.pvcElevation)} m\\nPVI: \${fmt(curve.pviStation)} m | cota \${fmt(curve.pviElevation)} m\\nPVT: \${fmt(curve.pvtStation)} m | cota \${fmt(curve.pvtElevation)} m\\nK: \${fmt(curve.kValue)} m/%\\nCota no meio da curva: \${fmt(mid)} m"
        }.getOrElse { "Erro: \${it.message}" } else "Confira os dados da curva vertical."
    }, modifier = Modifier.fillMaxWidth()) { Text("CALCULAR CURVA VERTICAL") }
    verticalResult?.let { ResultCard(it) }
}
