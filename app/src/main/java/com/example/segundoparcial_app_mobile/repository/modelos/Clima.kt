package com.example.segundoparcial_app_mobile.repository.modelos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Clima(
    val main: Main,
    val weather: List<Weather>,
    val name: String,
    var forecast: Forecast = Forecast(emptyList()) // valor por defecto
)

@Serializable
data class Main(
    val temp: Float,
    val humidity: Int
)

@Serializable
data class Weather(
    val main: String,
    val description: String
)
