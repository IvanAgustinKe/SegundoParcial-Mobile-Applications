package com.example.segundoparcial_app_mobile.presentation.clima

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.segundoparcial_app_mobile.data.SettingsRepository
import kotlinx.coroutines.launch
import kotlin.math.abs


data class DailyForecastUi(
    val dateStr: String,
    val min: Float,
    val max: Float,
    val description: String?,
    val icon: String?
)

@Composable
fun ClimaView(
    estado: ClimaEstado,
    loadIcon: suspend (String) -> ImageBitmap?,
    navController: NavController
) {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }
    val units by settings.unitsFlow.collectAsState(initial = "metric")
    val unitLabel = if (units == "imperial") "°F" else "°C"

    when (estado) {
        is ClimaEstado.Cargando -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ClimaEstado.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error al cargar el clima", color = MaterialTheme.colorScheme.error)
            }
        }
        is ClimaEstado.Mostrar -> {
            Column(modifier = Modifier.padding(16.dp, 32.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { navController.popBackStack() }) { Text("Volver") }
                    Button(onClick = { sharePronostico(context, estado) }) { Text("Compartir") }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = estado.clima.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )

                TemperatureView(estado, unitLabel, units, settings)

                Spacer(modifier = Modifier.height(16.dp))

                ForecastView(estado, unitLabel, loadIcon)
            }
        }
    }
}

@Composable
fun TemperatureView(
    estado: ClimaEstado.Mostrar,
    unitLabel: String,
    units: String,
    settings: SettingsRepository
) {
    val coroutineScope = rememberCoroutineScope()

    Text(
        text = buildAnnotatedString {
            append("${estado.clima.main.temp}")
            withStyle(
                style = SpanStyle(
                    fontSize = 32.sp,
                    baselineShift = BaselineShift.Superscript
                )
            ) { append(unitLabel) }
        },
        fontWeight = FontWeight.Bold,
        fontSize = 60.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(20.dp)
    )

    Text(
        text = "${estado.clima.weather.firstOrNull()?.main ?: "N/A"} | HR ${estado.clima.main.humidity}%",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = units == "metric",
                onClick = { coroutineScope.launch { settings.saveUnits("metric") } },
                label = { Text("°C") }
            )
            FilterChip(
                selected = units == "imperial",
                onClick = { coroutineScope.launch { settings.saveUnits("imperial") } },
                label = { Text("°F") }
            )
        }
    }
}

@Composable
fun ForecastView(
    estado: ClimaEstado.Mostrar,
    unitLabel: String,
    loadIcon: suspend (String) -> ImageBitmap?
) {
    Text(
        text = "Pronóstico:",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )

    Spacer(Modifier.height(8.dp))

    ForecastSparkline(
        temps = estado.clima.forecast.list.map { it.main.temp },
        unitLabel = unitLabel
    )

    Spacer(Modifier.height(12.dp))


    val daily = remember(estado.clima.forecast.list) {
        estado.clima.forecast.list
            .groupBy { it.dt_txt.substring(0, 10) } // "yyyy-MM-dd"
            .map { (dateStr, items) ->
                val min = items.minOf { it.main.temp_min }
                val max = items.maxOf { it.main.temp_max }

                // Slot representativo más cercano a 12:00
                val rep = items.minByOrNull { slot ->
                    val hh = slot.dt_txt.substring(11, 13).toIntOrNull() ?: 0
                    abs(hh - 12)
                } ?: items.first()

                val w = rep.weather.firstOrNull()
                DailyForecastUi(
                    dateStr = dateStr,
                    min = min,
                    max = max,
                    description = w?.main,
                    icon = w?.icon
                )
            }
            .sortedBy { it.dateStr }
            .take(5)
    }

    LazyRow(
        modifier = Modifier.padding(top = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(daily, key = { it.dateStr }) { d ->
            val iconBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
            LaunchedEffect(d.icon) { iconBitmap.value = d.icon?.let { loadIcon(it) } }

            Card(
                modifier = Modifier
                    .width(150.dp)
                    .padding(horizontal = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val (y, m, dStr) = d.dateStr.split("-")
                    Text("$dStr/$m", fontWeight = FontWeight.SemiBold)

                    iconBitmap.value?.let { img ->
                        Image(bitmap = img, contentDescription = null, modifier = Modifier.size(48.dp))
                    }

                    Text("${d.min.toInt()} $unitLabel / ${d.max.toInt()} $unitLabel")
                    d.description?.let { Text(it) }
                }
            }
        }
    }
}

@Composable
fun ForecastSparkline(
    temps: List<Float>,
    unitLabel: String,
    modifier: Modifier = Modifier
) {
    if (temps.isEmpty()) return

    val max = temps.maxOrNull() ?: 0f
    val min = temps.minOrNull() ?: 0f
    val range = (max - min).coerceAtLeast(0.0001f)

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary

    Canvas(
        modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceVariant)
            .padding(12.dp)
    ) {
        val stepX = if (temps.size > 1) size.width / (temps.size - 1) else 0f

        val linePath = Path()
        val fillPath = Path()

        temps.forEachIndexed { i, t ->
            val x = i * stepX
            val y = size.height - ((t - min) / range) * size.height
            if (i == 0) {
                linePath.moveTo(x, y)
                fillPath.moveTo(x, size.height)
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        fillPath.lineTo((temps.lastIndex) * stepX, size.height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(primary.copy(alpha = 0.25f), primary.copy(alpha = 0f))
            )
        )
        drawPath(
            path = linePath,
            color = primary,
            style = Stroke(width = 4.dp.toPx())
        )
        temps.forEachIndexed { i, t ->
            val x = i * stepX
            val y = size.height - ((t - min) / range) * size.height
            drawCircle(
                color = primary,
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}