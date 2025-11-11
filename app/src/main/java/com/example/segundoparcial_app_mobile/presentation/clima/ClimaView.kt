package com.example.segundoparcial_app_mobile.presentation.clima

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset


@Composable
fun ClimaView(
    estado: ClimaEstado,
    navController: NavController
) {
    val ctx = LocalContext.current
    val context = LocalContext.current
    val settings = remember { com.example.segundoparcial_app_mobile.data.SettingsRepository(context) }
    val units by settings.unitsFlow.collectAsState(initial = "metric")
    val coroutineScope = rememberCoroutineScope()
    val unitLabel = if (units == "imperial") "°F" else "°C"

    when (estado) {
        is ClimaEstado.Cargando -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ClimaEstado.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error al cargar el clima", color = MaterialTheme.colorScheme.error)
            }
        }
        is ClimaEstado.Mostrar -> {
            Column(modifier = Modifier.padding(16.dp)) {

                Button(onClick = { navController.popBackStack() }) { Text("Volver") }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { sharePronostico(ctx, estado) }) { Text("Compartir") }

                Spacer(Modifier.height(8.dp))

                Text(
                    "Ciudad: ${estado.clima.name}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text("Temperatura actual: ${estado.clima.main.temp} $unitLabel")
                Text("Humedad: ${estado.clima.main.humidity}%")
                Text("Estado: ${estado.clima.weather.firstOrNull()?.main ?: "N/A"}")

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pronóstico:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(Modifier.height(12.dp))

                ForecastSparkline(
                    temps = estado.clima.forecast.list.map { it.main.temp }, // o .take(8)
                    unitLabel = unitLabel
                )

                Spacer(Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.clima.forecast.list) { forecastItem ->
                        val clima = forecastItem.main
                        val descripcion = forecastItem.weather.firstOrNull()

                        Card(
                            modifier = Modifier
                                .width(200.dp)
                                .padding(horizontal = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("Día: ${forecastItem.dt_txt}")
                                Text("Temp: ${clima.temp} $unitLabel")
                                Text("Min: ${clima.temp_min} $unitLabel")
                                Text("Max: ${clima.temp_max} $unitLabel")
                                Text("Estado: ${descripcion?.main ?: "N/A"}")
                                Text("Icono: ${descripcion?.icon ?: "N/A"}")
                            }
                        }
                    }
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

    // Tomo colores **fuera** del Canvas
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
                fillPath.moveTo(x, size.height)  // para el relleno
                fillPath.lineTo(x, y)
            } else {
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }
        // cierro el polígono del relleno
        fillPath.lineTo((temps.lastIndex) * stepX, size.height)
        fillPath.close()

        // Relleno degradado sutil
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(primary.copy(alpha = 0.25f), primary.copy(alpha = 0f))
            )
        )

        // Línea
        drawPath(
            path = linePath,
            color = primary,
            style = Stroke(width = 4.dp.toPx())
        )

        // Puntos
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