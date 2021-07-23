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

package com.popalay.barnee.data.repository

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.util.capitalizeFirstChar
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.filter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface CollectionRepository {
    fun collections(): Flow<Set<Collection>>
    fun collection(name: String): Flow<Collection>
    fun collectionsUpdate(): Flow<Pair<Drink, Collection?>>
    suspend fun removeFromAllCollectionsAndNotify(drink: Drink)
    suspend fun addToCollectionAndNotify(collectionName: String = "", drink: Drink)
    suspend fun removeFromCollectionAndNotify(collectionName: String = "", drink: Drink)
    suspend fun remove(name: String)
    suspend fun saveOrMerge(name: String, aliases: Set<String>)
}

internal class CollectionRepositoryImpl(
    private val localStore: LocalStore,
    private val api: Api,
    private val json: Json
) : CollectionRepository {
    private val collectionsUpdateFlow = MutableSharedFlow<Pair<Drink, Collection?>>()

    init {
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch {
            runMigration()
        }
    }

    override suspend fun addToCollectionAndNotify(collectionName: String, drink: Drink) = withContext(Dispatchers.Default) {
        val collections = collections().first()
        val validCollectionName = collectionName.capitalizeFirstChar().ifBlank { Collection.DEFAULT_NAME }
        val targetCollection = (collections.firstOrNull { it.name == validCollectionName }
            ?: Collection(validCollectionName, emptySet(), emptySet())).let {
            it.copy(aliases = it.aliases + drink.alias, cover = setOf(drink.displayImageUrl) + it.cover)
        }

        val newCollections = collections.toMutableSet().apply {
            removeAll { it.name == targetCollection.name }
            add(targetCollection)
            toSet()
            saveCollections(this)
        }

        collectionsUpdateFlow.emit(drink.copy(userCollections = newCollections.filter(drink)) to targetCollection)
    }

    override suspend fun removeFromCollectionAndNotify(collectionName: String, drink: Drink) = withContext(Dispatchers.Default) {
        val collections = collections().first()
        val validCollectionName = collectionName.capitalizeFirstChar().ifBlank { Collection.DEFAULT_NAME }
        val targetCollection = collections.firstOrNull { it.name == validCollectionName }?.let {
            it.copy(aliases = it.aliases - drink.alias, cover = it.cover - drink.displayImageUrl)
        } ?: return@withContext

        val newCollections = collections.toMutableSet().apply {
            removeAll { it.name == targetCollection.name }
            add(targetCollection)
            toSet()
            saveCollections(this)
        }

        collectionsUpdateFlow.emit(drink.copy(userCollections = newCollections.filter(drink)) to null)
    }

    override suspend fun removeFromAllCollectionsAndNotify(drink: Drink) = withContext(Dispatchers.Default) {
        val collections = removeFromCollections(drink)
        saveCollections(collections)
        collectionsUpdateFlow.emit(drink.copy(userCollections = emptyList()) to null)
    }

    override suspend fun remove(name: String) = withContext(Dispatchers.Default) {
        val collections = collections().first()
        collections.filter { it.name != name }.run {
            saveCollections(toSet())
        }
    }

    override suspend fun saveOrMerge(name: String, aliases: Set<String>) = withContext(Dispatchers.Default) {
        val collections = collections().first()
        val targetCollection = aliases.takeIf { it.any(String::isNotEmpty) }
            ?.let { runCatching { api.drinksByAliases(it, skip = 0, take = 100) }.getOrNull() }
            ?.let { drinks ->
                (collections().first().firstOrNull { it.name == name } ?: Collection(name = name)).let { collection ->
                    collection.copy(
                        aliases = collection.aliases + drinks.map { it.alias }.toSet(),
                        cover = collection.cover + drinks.map { it.displayImageUrl }.toSet()
                    )
                }
            } ?: return@withContext

        collections.toMutableSet().apply {
            removeAll { it.name == targetCollection.name }
            add(targetCollection)
            saveCollections(toSet())
        }
    }

    override fun collections(): Flow<Set<Collection>> = localStore.asFlow(KEY_COLLECTION)
        .map { collectionsJson ->
            try {
                json.decodeFromString<Set<Collection>>(collectionsJson).sortedBy { it.name }.toSet()
            } catch (ignore: SerializationException) {
                emptySet()
            }
        }

    override fun collection(name: String): Flow<Collection> = collections()
        .mapNotNull { collection -> collection.firstOrNull { it.name == name } }

    override fun collectionsUpdate(): Flow<Pair<Drink, Collection?>> = collectionsUpdateFlow.asSharedFlow()

    private suspend fun saveCollections(collections: Set<Collection>) {
        val serialized = json.encodeToString(collections)
        localStore.save(KEY_COLLECTION, serialized)
    }

    private suspend fun removeFromCollections(drink: Drink): Set<Collection> {
        val collections = collections().first()
        val targetCollections = collections.filter(drink)
        val resultCollections = targetCollections
            .map {
                it.copy(
                    aliases = it.aliases - drink.alias,
                    cover = it.cover - drink.displayImageUrl
                )
            }
            .filter { it.aliases.isNotEmpty() }

        return collections.toMutableSet().apply {
            removeAll { it in targetCollections }
            if (resultCollections.isNotEmpty()) addAll(resultCollections)
        }.toSet()
    }

    private suspend fun runMigration() {
        val favoriteAliases = localStore.setAsFlow(KEY_FAVORITE_DRINKS).firstOrNull() ?: return
        saveOrMerge(KEY_FAVORITE_DRINKS, favoriteAliases)
        localStore.remove(KEY_FAVORITE_DRINKS)
    }

    companion object {
        private const val KEY_FAVORITE_DRINKS = "KEY_FAVORITE_DRINKS"
        private const val KEY_COLLECTION = "KEY_COLLECTION"
    }
}
