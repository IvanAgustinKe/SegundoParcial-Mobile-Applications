package com.example.segundoparcial_app_mobile.repository


import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad
import com.example.segundoparcial_app_mobile.repository.modelos.Clima
import com.example.segundoparcial_app_mobile.repository.modelos.Forecast

interface Repositorio {
    suspend fun buscarCiudad(ciudad: String): List<Ciudad>
    suspend fun traerClima(lat: Float, lon: Float): Clima
    suspend fun traerForecast(lat: Float, lon: Float): Forecast
}