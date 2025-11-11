package com.example.segundoparcial_app_mobile.presentation.ciudades


import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad

sealed class CiudadesEstado {
    object Vacio : CiudadesEstado()
    object Cargando : CiudadesEstado()
    data class Resultado(val ciudades: List<Ciudad>) : CiudadesEstado()
    data class Error(val mensaje: String = "Error al buscar ciudades") : CiudadesEstado()
}