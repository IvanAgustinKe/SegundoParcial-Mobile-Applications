package com.example.segundoparcial_app_mobile.presentation.clima

import android.content.Context
import android.content.Intent

fun sharePronostico(context: Context, estado: ClimaEstado.Mostrar) {
    val clima = estado.clima
    val current = clima.weather.firstOrNull()

    val resumen = buildString {
        appendLine("Pronóstico - ${clima.name}")
        appendLine("Ahora: ${clima.main.temp} °C | Humedad: ${clima.main.humidity}%")
        appendLine("Estado: ${current?.description ?: current?.main ?: "N/D"}")
        val items = clima.forecast.list.take(5)
        if (items.isNotEmpty()) {
            appendLine()
            appendLine("Próximos:")
            items.forEach { f ->
                appendLine("- ${f.dt_txt}: ${f.main.temp} °C (min ${f.main.temp_min} / max ${f.main.temp_max}) ${f.weather.firstOrNull()?.main.orEmpty()}")
            }
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, resumen.trim())
    }
    context.startActivity(Intent.createChooser(intent, "Compartir pronóstico"))
}
