package com.example.segundoparcial_app_mobile.presentation.ciudades

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.navigation.NavHostController
import com.example.segundoparcial_app_mobile.data.SettingsRepository
import com.example.segundoparcial_app_mobile.repository.RepositorioApi
import com.example.segundoparcial_app_mobile.router.Ruta
import com.example.segundoparcial_app_mobile.router.Router
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember

@Composable
fun CiudadesPage(
    navController: NavHostController
) {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }

    val viewModel: CiudadesViewModel = viewModel(
        factory = CiudadesViewModelFactory(
            repositorio = RepositorioApi(),
            router = Router(navController),
            settings = settings
        )
    )

    //  última ciudad guardada
    val ultimaCiudad by settings.selectedCity.collectAsState(initial = null)

    Column(Modifier.fillMaxSize()) {
            // Vista actual
        CiudadesView(
            state = viewModel.uiState,
            onAction = { intencion -> viewModel.ejecutar(intencion) },
            ultimaCiudad = ultimaCiudad,
            onAbrirUltima = {
                ultimaCiudad?.let { c ->
                    Router(navController).navegar(Ruta.Clima(c.name, c.lat, c.lon))
                }
            }
        )
    }
}