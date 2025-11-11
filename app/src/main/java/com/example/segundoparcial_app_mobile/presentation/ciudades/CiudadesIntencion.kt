package com.example.segundoparcial_app_mobile.presentation.ciudades


import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad

sealed class CiudadesIntencion {
    data class Buscar(val nombre: String) : CiudadesIntencion()
    data class Seleccionar(val ciudad: Ciudad) : CiudadesIntencion()
}
