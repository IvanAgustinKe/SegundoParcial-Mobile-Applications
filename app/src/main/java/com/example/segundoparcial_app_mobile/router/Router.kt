package com.example.segundoparcial_app_mobile.router

import androidx.navigation.NavHostController

sealed class Ruta(val ruta: String) {
    data object Ciudades : Ruta("ciudades")
    data class Clima(
        val nombre: String,
        val lat: Float,
        val lon: Float
    ) : Ruta("clima/$nombre/$lat/$lon")
}

class Router(private val navController: NavHostController) {
    fun navegar(ruta: Ruta) {
        try{
            navController.navigate(ruta.ruta)
        }catch(e: Exception) {
            print(e)
        }
    }
}
