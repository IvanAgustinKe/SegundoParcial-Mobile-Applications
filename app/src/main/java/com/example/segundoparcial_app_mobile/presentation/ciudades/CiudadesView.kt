@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.segundoparcial_app_mobile.presentation.ciudades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad

@Composable
fun CiudadesView(
    state: CiudadesEstado,
    onAction: (CiudadesIntencion) -> Unit,
    ultimaCiudad: Ciudad?,
    onAbrirUltima: () -> Unit
) {
    var textoBusqueda by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        textoBusqueda = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CitySearchBar(
                query = textoBusqueda,
                onQueryChange = { textoBusqueda = it },
                onSearch = {
                    if (textoBusqueda.isNotBlank()) {
                        onAction(CiudadesIntencion.Buscar(textoBusqueda))
                    }
                },
                lastCity = ultimaCiudad,
                onLastClick = onAbrirUltima,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (textoBusqueda.isNotBlank()) {
                    onAction(CiudadesIntencion.Buscar(textoBusqueda))
                }
            }) { Text("Buscar") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val alignment = when (state) {
            is CiudadesEstado.Resultado -> Alignment.TopStart
            else -> Alignment.Center
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = alignment
        ) {
            when (state) {
                is CiudadesEstado.Vacio -> Text("No se encontraron resultados.")
                is CiudadesEstado.Cargando -> CircularProgressIndicator()
                is CiudadesEstado.Error -> Text("Hubo un error al buscar.")
                is CiudadesEstado.Resultado -> ListaCiudades(state.ciudades, onAction)
            }
        }
    }
}


@Composable
private fun CitySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    lastCity: Ciudad?,
    onLastClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Buscar ciudad...")
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            Row {
                if (lastCity != null) {
                    IconButton(
                        onClick = onLastClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            Icons.Outlined.History,
                            contentDescription = "Última: ${lastCity.name}",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.padding(8.dp, 0.dp).size(30.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Limpiar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ListaCiudades(
    ciudades: List<Ciudad>,
    onAction: (CiudadesIntencion) -> Unit
) {
    LazyColumn {
        items(ciudades) { ciudad ->
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = { onAction(CiudadesIntencion.Seleccionar(ciudad)) }
            ) {
                Text(
                    text = "${ciudad.name}, ${ciudad.state}, ${ciudad.country}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
