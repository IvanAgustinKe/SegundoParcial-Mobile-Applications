package com.example.segundoparcial_app_mobile.presentation.clima


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.segundoparcial_app_mobile.repository.Repositorio
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

class ClimaViewModel(
    private val repositorio: Repositorio
) : ViewModel() {

    var estado by mutableStateOf<ClimaEstado>(ClimaEstado.Cargando)
        private set

    fun cargarClima(lat: Float, lon: Float) {
        estado = ClimaEstado.Cargando
        viewModelScope.launch {
            try {
                val clima = repositorio.traerClima(lat, lon)
                val forecast = repositorio.traerForecast(lat, lon)
                clima.forecast = forecast
                estado = ClimaEstado.Mostrar(clima)
            } catch (e: Exception) {
                estado = ClimaEstado.Error
            }
        }
    }
}

class ClimaViewModelFactory(
    private val repositorio: Repositorio
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClimaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClimaViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
