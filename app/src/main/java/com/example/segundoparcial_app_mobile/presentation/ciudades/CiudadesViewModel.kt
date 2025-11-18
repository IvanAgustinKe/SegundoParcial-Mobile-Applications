package com.example.segundoparcial_app_mobile.presentation.ciudades
import android.util.Log
import com.example.segundoparcial_app_mobile.data.SettingsRepository


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad
import kotlinx.coroutines.launch
import com.example.segundoparcial_app_mobile.repository.RepositorioApi
import com.example.segundoparcial_app_mobile.repository.Repositorio
import com.example.segundoparcial_app_mobile.router.Ruta
import com.example.segundoparcial_app_mobile.router.Router

class CiudadesViewModel(
    private val repositorio: RepositorioApi,
    private val router : Router,
    private val settings: SettingsRepository
) : ViewModel() {

    var uiState by mutableStateOf<CiudadesEstado>(CiudadesEstado.Vacio)

    fun ejecutar(intencion: CiudadesIntencion) {
        when (intencion) {
            is CiudadesIntencion.Buscar -> buscar(intencion.nombre)
            is CiudadesIntencion.Seleccionar -> seleccionar(intencion.ciudad)
        }
    }


    private fun buscar(nombre: String) {
        uiState = CiudadesEstado.Cargando
        viewModelScope.launch {
            try {
                val resultado = repositorio.buscarCiudad(nombre)
                if (resultado.isEmpty()) {
                    uiState = CiudadesEstado.Vacio
                } else {
                    uiState = CiudadesEstado.Resultado(resultado)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("CiudadesViewModel", "Error buscando ciudad: ${e.message}", e)
                uiState = CiudadesEstado.Error()
            }
        }
    }

    private fun seleccionar(ciudad: Ciudad) {
        viewModelScope.launch { settings.saveCity(ciudad) }

        router.navegar(Ruta.Clima(nombre = ciudad.name, lat = ciudad.lat, lon = ciudad.lon))

        // limpia resultados para que al volver la lista esté vacía
        uiState = CiudadesEstado.Vacio
    }
}

    class CiudadesViewModelFactory(
        private val repositorio: RepositorioApi,
        private val router: Router,
        private val settings: SettingsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CiudadesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CiudadesViewModel(repositorio, router, settings) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
