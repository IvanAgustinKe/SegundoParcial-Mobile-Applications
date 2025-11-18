package com.example.segundoparcial_app_mobile.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.segundoparcial_app_mobile.repository.modelos.Ciudad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val CITY_NAME = stringPreferencesKey("city_name")
        val CITY_LAT  = floatPreferencesKey("city_lat")
        val CITY_LON  = floatPreferencesKey("city_lon")
        val UNITS     = stringPreferencesKey("units")
    }

    /** Última ciudad seleccionada (o null si no hay) */
    val selectedCity: Flow<Ciudad?> = context.dataStore.data.map { prefs ->
        val name = prefs[Keys.CITY_NAME] ?: return@map null
        val lat  = prefs[Keys.CITY_LAT] ?: return@map null
        val lon  = prefs[Keys.CITY_LON] ?: return@map null
        Ciudad(name = name, lat = lat, lon = lon, state = "", country = "")
    }

    /** Unidades preferidas */
    val unitsFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.UNITS] ?: "metric"
    }

    /** Guardar ciudad seleccionada */
    suspend fun saveCity(ciudad: Ciudad) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CITY_NAME] = ciudad.name
            prefs[Keys.CITY_LAT]  = ciudad.lat
            prefs[Keys.CITY_LON]  = ciudad.lon
        }
    }

    /** Guardar unidades */
    suspend fun saveUnits(units: String) {
        context.dataStore.edit { it[Keys.UNITS] = units }
    }

    suspend fun clearCity() {
        context.dataStore.edit {
            it.remove(Keys.CITY_NAME)
            it.remove(Keys.CITY_LAT)
            it.remove(Keys.CITY_LON)
        }
    }
}
