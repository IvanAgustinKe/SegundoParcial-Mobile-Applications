package com.example.segundoparcial_app_mobile.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad
import com.example.segundoparcial_app_mobile.repository.modelos.Clima
import com.example.segundoparcial_app_mobile.repository.modelos.Forecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.readBytes
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

class RepositorioApi : Repositorio {

    private val apikey = "9a692ca1d082e061ef1c8cc0e0fe5fe2"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        val respuesta = client.get("https://api.openweathermap.org/geo/1.0/direct") {
            parameter("q", ciudad)
            parameter("limit", 100)
            parameter("appid", apikey)
        }
        if (respuesta.status == HttpStatusCode.OK) {
            return respuesta.body()
        } else {
            throw Exception("Error buscando ciudad: ${respuesta.status}")
        }
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        val respuesta = client.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", "metric")
            parameter("lang", "es")
            parameter("appid", apikey)
        }
        if (respuesta.status == HttpStatusCode.OK) {
            return respuesta.body()
        } else {
            throw Exception("Error trayendo clima: ${respuesta.status}")
        }
    }

    override suspend fun traerForecast(lat: Float, lon: Float): Forecast {
        val respuesta = client.get("https://api.openweathermap.org/data/2.5/forecast") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", "metric")
            parameter("lang", "es")
            parameter("appid", apikey)
        }
        if (respuesta.status == HttpStatusCode.OK) {
            return respuesta.body()
        } else {
            throw Exception("Error trayendo forecast: ${respuesta.status}")
        }
    }

    override suspend fun traerIcon(iconCode: String): Bitmap? {
        return try {
            val respuesta = client.get("https://openweathermap.org/img/wn/${iconCode}@2x.png")
            val bytes = respuesta.readBytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
