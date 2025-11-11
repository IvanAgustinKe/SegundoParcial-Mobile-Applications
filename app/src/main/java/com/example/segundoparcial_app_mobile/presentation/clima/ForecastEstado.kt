package com.example.segundoparcial_app_mobile.presentation.clima

import com.example.segundoparcial_app_mobile.repository.modelos.Forecast

sealed class ForecastEstado {
    object Cargando : ForecastEstado()
    object Error : ForecastEstado()
    data class Mostrar(val forecast: Forecast) : ForecastEstado()
}
