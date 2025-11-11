package com.example.segundoparcial_app_mobile.repository


import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad
import com.example.segundoparcial_app_mobile.repository.modelos.Clima
import com.example.segundoparcial_app_mobile.repository.modelos.Forecast
import io.ktor.client.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*


class RepositorioApi : Repositorio {

    private val apikey = "43add3e61ffb5e8c900e18a0c0dc546b"
    private  val client = HttpClient(){
        install(ContentNegotiation){
            json(Json{
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        val respuesta = client.get("https://api.openweathermap.org/geo/1.0/direct"){
            parameter("q", ciudad)
            parameter("limit", 100)
            parameter("appid", apikey)
        }

        if(respuesta.status == HttpStatusCode.OK){
            val ciudades = respuesta.body<List<Ciudad>>()
            return ciudades
        }else{
            throw Exception()
        }
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        val respuesta = client.get("https://api.openweathermap.org/data/2.5/weather"){
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", "metric")
            parameter("lang", "es")
            parameter("appid", apikey)
        }

        if(respuesta.status == HttpStatusCode.OK){
            var clima = respuesta.body<Clima>()
            return clima
        }else{
            throw Exception()
        }
    }

    override suspend fun traerForecast(lat: Float, lon: Float): Forecast {
        val respuesta = client.get("https://api.openweathermap.org/data/2.5/forecast") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", "metric")
            parameter("lang", "es")
            parameter("cnt", 7)
            parameter("appid", apikey)
        }

        if (respuesta.status == HttpStatusCode.OK) {
            return respuesta.body<Forecast>()
        } else {
            throw Exception()
        }
    }
}