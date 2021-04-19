package com.popalay.barnee.data.repository

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.remote.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class DrinkRepository(
    private val api: Api,
    private val localStore: LocalStore
) {
    suspend fun getRandomDrinks(count: Int): Flow<List<Drink>> = mapFavorites(api.randomDrinks(count))

    suspend fun getDrinksByTags(tags: List<String>): Flow<List<Drink>> = mapFavorites(api.drinksByTags(tags))

    suspend fun getSimilarDrinksFor(alias: String): Flow<List<Drink>> = mapFavorites(api.similarDrinks(alias))

    suspend fun searchDrinks(
        query: String,
        filters: Map<String, List<String>>,
        count: Int = 100
    ): Flow<List<Drink>> {
        val searchRequest = filters
            .filter { it.key.isNotBlank() && it.value.isNotEmpty() }
            .map { it.key + "/" + it.value.joinToString(",") }
            .joinToString(separator = "/", prefix = query.takeIf { it.isNotBlank() }?.let { "search/$it/" } ?: "")

        return mapFavorites(api.searchDrinks(searchRequest, count))
    }

    suspend fun getAggregation(): Flow<Aggregation> = flowOf(api.getAggregation())

    fun getFavoriteDrinks(): Flow<List<Drink>> = localStore.getFavoriteDrinks()
        .map { favorites -> api.drinksByAliases(favorites.toList()).map { it.copy(isFavorite = true) } }

    fun getFullDrink(alias: String): Flow<FullDrinkResponse> = flow { emit(api.getFullDrink(alias)) }
        .flatMapLatest { response ->
            localStore.getFavoriteDrinks()
                .map { favorites ->
                    response.copy(
                        relatedDrinks = response.relatedDrinks.map { it.copy(isFavorite = it.alias in favorites) },
                        result = response.result.map { it.copy(isFavorite = it.alias in favorites) }
                    )
                }
        }

    suspend fun toggleFavoriteFor(alias: String): Boolean = withContext(Dispatchers.Main) {
        val favorites = localStore.getFavoriteDrinks().first()
        val isInFavorites = alias in favorites
        if (isInFavorites) {
            removeFromFavorites(alias)
        } else {
            saveAsFavorite(alias)
        }
        !isInFavorites
    }

    private suspend fun saveAsFavorite(alias: String) {
        localStore.saveFavorite(alias)
    }

    private suspend fun removeFromFavorites(alias: String) {
        localStore.removeFavorite(alias)
    }

    private fun mapFavorites(drinks: List<Drink>) = localStore.getFavoriteDrinks()
        .map { favorites -> drinks.map { it.copy(isFavorite = it.alias in favorites) } }
}