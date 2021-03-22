package com.popalay.barnee.data.local

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private object PreferencesKeys {
    val FAVORITES_DRINKS = stringSetPreferencesKey("favorites-drinks")
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("StaticFieldLeak")
actual object LocalStore {

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    actual fun getFavoriteDrinks(): Flow<Set<String>> = context.dataStore.data
        .map { it[PreferencesKeys.FAVORITES_DRINKS] ?: emptySet() }

    actual suspend fun saveFavorite(alias: String) {
        context.dataStore.updateData { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITES_DRINKS] ?: emptySet()
            preferences.toMutablePreferences().apply {
                this[PreferencesKeys.FAVORITES_DRINKS] = currentFavorites + alias
            }
        }
    }

    actual suspend fun removeFavorite(alias: String) {
        context.dataStore.updateData { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITES_DRINKS] ?: emptySet()
            preferences.toMutablePreferences().apply {
                this[PreferencesKeys.FAVORITES_DRINKS] = currentFavorites - alias
            }
        }
    }
}
