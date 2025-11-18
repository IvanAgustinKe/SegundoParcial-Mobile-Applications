package com.example.segundoparcial_app_mobile.presentation.clima

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.segundoparcial_app_mobile.repository.RepositorioApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController


@Composable
fun ClimaPage(
    navController: NavController,
    lat: Float,
    nombre: String,
    lon: Float
) {
    val viewModel: ClimaViewModel = viewModel(
        factory = ClimaViewModelFactory(RepositorioApi())
    )

    // Al entrar a la pantalla, cargar el clima
    LaunchedEffect(Unit) {
        viewModel.cargarClima(lat, lon)
    }

    ClimaView(estado = viewModel.estado, loadIcon = viewModel::cargarIcono, navController = navController)
}
