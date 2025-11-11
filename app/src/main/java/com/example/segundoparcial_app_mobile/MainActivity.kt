package com.example.segundoparcial_app_mobile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.segundoparcial_app_mobile.presentation.ciudades.CiudadesPage
import com.example.segundoparcial_app_mobile.presentation.clima.ClimaPage
import com.example.segundoparcial_app_mobile.router.Ruta
import com.example.segundoparcial_app_mobile.router.Router
import com.example.segundoparcial_app_mobile.ui.theme.segundoparcial_app_mobile_Theme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            segundoparcial_app_mobile_Theme {
                val navController = rememberNavController()
                val router = Router(navController)

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Ruta.Ciudades.ruta
                    ) {
                        composable(Ruta.Ciudades.ruta) {
                            CiudadesPage(navController = navController)
                        }
                        composable("clima/{nombre}/{lat}/{lon}") { backStackEntry ->
                            val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
                            val lat = backStackEntry.arguments?.getString("lat")?.toFloatOrNull() ?: 0f
                            val lon = backStackEntry.arguments?.getString("lon")?.toFloatOrNull() ?: 0f
                            ClimaPage(nombre = nombre, lat = lat, lon = lon, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
