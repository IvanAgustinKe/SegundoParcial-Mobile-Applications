@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.segundoparcial_app_mobile.presentation.ciudades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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

        when (state) {
            is CiudadesEstado.Vacio -> Text("No se encontraron resultados.")
            is CiudadesEstado.Cargando -> CircularProgressIndicator()
            is CiudadesEstado.Error -> Text("Hubo un error al buscar.")
            is CiudadesEstado.Resultado -> ListaCiudades(state.ciudades, onAction)
        }
    }
}

/** SearchBar “Última búsqueda”. */
@Composable
private fun CitySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    lastCity: Ciudad?,
    onLastClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var active by remember { mutableStateOf(false) }

    DockedSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {
            active = false
            onSearch()
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Buscar ciudad") },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Limpiar")
                }
            }
        },
        modifier = modifier
    ) {
        // Contenido del menú (cuando active = true)
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            if (lastCity != null) {
                SuggestionChip(
                    onClick = { onLastClick(); active = false },
                    label = {
                        Text(
                            "Última búsqueda: ${lastCity.name}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },

                    icon = { Icon(Icons.Outlined.History, contentDescription = null) }
                )
            }
        }
    }
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
                    text = "${ciudad.name}, ${ciudad.country}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
