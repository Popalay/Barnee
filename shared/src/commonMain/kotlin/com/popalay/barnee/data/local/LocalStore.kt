package com.popalay.barnee.data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalSettingsApi::class)
class LocalStore(
    private val settings: FlowSettings
) {
    companion object {
        private const val KEY_FAVORITE_DRINKS = "KEY_FAVORITE_DRINKS"
        private const val STRING_SEPARATOR = "::"
    }

    fun getFavoriteDrinks(): Flow<Set<String>> = settings.getStringFlow(KEY_FAVORITE_DRINKS, "")
        .map { it.split(STRING_SEPARATOR).toSet() }

    suspend fun saveFavorite(alias: String) {
        val currentFavorites = settings.getStringOrNull(KEY_FAVORITE_DRINKS)?.split(STRING_SEPARATOR)?.toSet() ?: emptySet()
        settings.putString(KEY_FAVORITE_DRINKS, (currentFavorites + alias).joinToString(STRING_SEPARATOR))
    }

    suspend fun removeFavorite(alias: String) {
        val currentFavorites = settings.getStringOrNull(KEY_FAVORITE_DRINKS)?.split(STRING_SEPARATOR)?.toSet() ?: emptySet()
        settings.putString(KEY_FAVORITE_DRINKS, (currentFavorites - alias).joinToString(STRING_SEPARATOR))
    }
}
