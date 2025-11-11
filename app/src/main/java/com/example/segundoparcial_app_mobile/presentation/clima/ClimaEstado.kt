package com.example.segundoparcial_app_mobile.presentation.clima

import com.example.segundoparcial_app_mobile.repository.modelos.Clima

sealed class ClimaEstado {
    object Cargando : ClimaEstado()
    object Error : ClimaEstado()
    data class Mostrar(val clima: Clima) : ClimaEstado()
}
