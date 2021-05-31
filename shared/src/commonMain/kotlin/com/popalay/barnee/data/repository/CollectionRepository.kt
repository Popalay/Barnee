package com.popalay.barnee.data.repository

import com.popalay.barnee.data.local.LocalStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest

class CollectionRepository(
    private val localStore: LocalStore
) {
    fun getCollection(collection: String = KEY_FAVORITE_DRINKS): Flow<Set<String>> =
        localStore.getSetFlow(collection)

    fun getAllCollections(): Flow<List<Set<String>>> = localStore.getSetFlow(KEY_COLLECTION)
        .mapLatest { collections ->
            val list = mutableListOf<Set<String>>()
            collections.forEach {
                list += getCollection(it).first()
            }
            list
        }

    suspend fun saveToCollection(alias: String, collection: String = KEY_FAVORITE_DRINKS) {
        localStore.saveToSet(alias, collection) {
            localStore.saveToSet(collection, KEY_COLLECTION)
        }
    }

    suspend fun removeFromCollection(alias: String, collection: String = KEY_FAVORITE_DRINKS) {
        localStore.removeFromSet(alias, collection) {
            localStore.removeFromSet(collection, KEY_COLLECTION)
        }
    }

    companion object {
        private const val KEY_FAVORITE_DRINKS = "KEY_FAVORITE_DRINKS"
        private const val KEY_COLLECTION = "KEY_COLLECTION"
    }
}