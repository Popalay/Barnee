package com.popalay.barnee.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
object LocalStore {

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    fun getFavoriteDrinks(): Flow<Set<String>> = context.dataStore.data
        .map { it[PreferencesKeys.FAVORITES_DRINKS] ?: emptySet() }

    suspend fun saveFavorite(alias: String) {
        context.dataStore.updateData { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITES_DRINKS] ?: emptySet()
            Log.d("ssss", "current = $currentFavorites")
            preferences.toMutablePreferences().apply {
                this[PreferencesKeys.FAVORITES_DRINKS] = currentFavorites + alias
            }
        }
    }

    suspend fun removeFavorite(alias: String) {
        context.dataStore.updateData { preferences ->
            val currentFavorites = preferences[PreferencesKeys.FAVORITES_DRINKS] ?: emptySet()
            Log.d("ssss", "current = $currentFavorites")
            preferences.toMutablePreferences().apply {
                this[PreferencesKeys.FAVORITES_DRINKS] = currentFavorites - alias
            }
        }
    }
}
