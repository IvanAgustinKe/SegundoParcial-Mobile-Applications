package com.example.segundoparcial_app_mobile.repository.modelos


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val list: List<ForecastItem>
)

@Serializable
data class ForecastItem(
    val dt_txt: String,
    val main: MainForecast,
    val weather: List<WeatherForecast>
)

@Serializable
data class MainForecast(
    val temp: Float,
    val temp_min: Float,
    val temp_max: Float
)

@Serializable
data class WeatherForecast(
    val main: String,
    val icon: String
)
