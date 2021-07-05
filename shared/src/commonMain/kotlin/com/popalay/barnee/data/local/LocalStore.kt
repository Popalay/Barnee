/*
 * Copyright (c) 2021 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
