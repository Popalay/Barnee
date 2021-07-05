package com.popalay.barnee.data.local

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalStore {
    suspend fun save(key: String, value: String)
    suspend fun remove(key: String)
    suspend fun saveToSet(key: String, value: String, doOnEmpty: suspend () -> Unit = {})
    suspend fun removeFromSet(key: String, value: String, doOnEmpty: suspend () -> Unit = {})
    fun asFlow(key: String): Flow<String>
    fun setAsFlow(key: String): Flow<Set<String>>
}

@OptIn(ExperimentalSettingsApi::class)
internal class LocalStoreImpl(
    private val settings: FlowSettings
) : LocalStore {
    override suspend fun saveToSet(
        key: String,
        value: String,
        doOnEmpty: suspend () -> Unit
    ) {
        val currentItems = settings.getStringOrNull(key)?.split(STRING_SEPARATOR)?.toSet() ?: emptySet()
        val newItems = (currentItems + value).joinToString(STRING_SEPARATOR)
        settings.putString(key, newItems)
        if (currentItems.isEmpty()) doOnEmpty()
    }

    override suspend fun save(
        key: String,
        value: String
    ) {
        settings.putString(key, value)
    }

    override suspend fun remove(key: String) {
        settings.remove(key)
    }

    override suspend fun removeFromSet(
        key: String,
        value: String,
        doOnEmpty: suspend () -> Unit
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

    override fun asFlow(key: String): Flow<String> = settings.getStringFlow(key, "")

    override fun setAsFlow(key: String): Flow<Set<String>> = settings.getStringFlow(key, "")
        .map { it.split(STRING_SEPARATOR).toSet() }

    companion object {
        private const val STRING_SEPARATOR = "::"
    }
}
