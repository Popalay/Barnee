package com.popalay.barnee.data.repository

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.Receipt
import com.popalay.barnee.data.remote.Api
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class DrinkRepository(
    private val api: Api,
    private val localStore: LocalStore
) {
    suspend fun getRandomDrinks(count: Int): Flow<List<Drink>> = flow { emit(api.randomDrinks(count)) }
        .flatMapLatest { mapFavorites(it) }

    suspend fun getDrinksByAliases(aliases: List<String>): Flow<List<Drink>> = flow { emit(api.drinksByAliases(aliases)) }
        .flatMapLatest { mapFavorites(it) }

    suspend fun getDrinksByTags(tags: List<String>): Flow<List<Drink>> = flow { emit(api.drinksByTags(tags)) }
        .flatMapLatest { mapFavorites(it) }

    suspend fun getSimilarDrinksFor(alias: String): Flow<List<Drink>> = flow { emit(api.similarDrinks(alias)) }
        .flatMapLatest { mapFavorites(it) }

    suspend fun searchDrinks(
        query: String,
        filters: Map<String, List<String>>
    ): Flow<List<Drink>> {
        val searchRequest = filters
            .filter { it.key.isNotBlank() && it.value.isNotEmpty() }
            .map { it.key + "/" + it.value.joinToString(",") }
            .joinToString(separator = "/", prefix = query.takeIf { it.isNotBlank() }?.let { "search/$it/" } ?: "")

        return flow { emit(api.searchDrinks(searchRequest)) }
            .flatMapLatest { mapFavorites(it) }
    }

    suspend fun getAggregation(): Aggregation = api.getAggregation()

    fun getFavoriteDrinks(): Flow<List<Drink>> = localStore.getFavoriteDrinks()
        .map { favorites -> api.drinksByAliases(favorites.toList()).map { it.copy(isFavorite = true) } }

    fun getReceipt(alias: String): Flow<Receipt> = flow { emit(api.getReceipt(alias)) }
        .flatMapLatest { receipt ->
            localStore.getFavoriteDrinks()
                .map { receipt.copy(isFavorite = alias in it) }
        }

    suspend fun saveAsFavorite(alias: String) {
        localStore.saveFavorite(alias)
    }

    suspend fun removeFromFavorites(alias: String) {
        localStore.removeFavorite(alias)
    }

    suspend fun toggleFavoriteFor(alias: String): Boolean {
        val favorites = localStore.getFavoriteDrinks().first()
        val isInFavorites = alias in favorites
        if (isInFavorites) {
            removeFromFavorites(alias)
        } else {
            saveAsFavorite(alias)
        }
        return !isInFavorites
    }

    private fun mapFavorites(drinks: List<Drink>) = localStore.getFavoriteDrinks()
        .map { favorites -> drinks.map { it.copy(isFavorite = it.alias in favorites) } }
}