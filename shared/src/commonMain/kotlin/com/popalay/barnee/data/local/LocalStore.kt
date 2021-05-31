package com.popalay.barnee.data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalSettingsApi::class)
class LocalStore(
    private val settings: FlowSettings
) {
    suspend fun saveToSet(
        value: String,
        key: String,
        doOnEmpty: suspend () -> Unit = {}
    ) {
        val currentItems = settings.getStringOrNull(key)?.split(STRING_SEPARATOR)?.toSet() ?: emptySet()
        val newItems = (currentItems + value).joinToString(STRING_SEPARATOR)
        settings.putString(key, newItems)
        if (currentItems.isEmpty()) doOnEmpty()
    }

    suspend fun removeFromSet(
        value: String,
        key: String,
        doOnEmpty: suspend () -> Unit = {}
    ) {
        val currentItems = settings.getStringOrNull(key)?.split(STRING_SEPARATOR)?.toSet() ?: emptySet()
        val newItems = (currentItems - value).joinToString(STRING_SEPARATOR)
        if (newItems.isEmpty()) {
            settings.remove(key)
            doOnEmpty()
        } else {
            settings.putString(key, newItems)
        }
    }

    fun getSetFlow(key: String): Flow<Set<String>> = settings.getStringFlow(key, "")
        .map { it.split(STRING_SEPARATOR).toSet() }

    companion object {
        private const val STRING_SEPARATOR = "::"
    }
}
