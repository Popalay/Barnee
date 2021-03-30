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

class DrinkRepository(
    private val api: Api,
    private val localStore: LocalStore
) {
    suspend fun getRandomDrinks(count: Int): List<Drink> = api.randomDrinks(count)

    suspend fun getDrinksByAliases(aliases: List<String>): List<Drink> = api.drinksByAliases(aliases)

    suspend fun getDrinksByTags(tags: List<String>): List<Drink> = api.drinksByTags(tags)

    suspend fun getSimilarDrinksFor(alias: String): List<Drink> = api.similarDrinks(alias)

    suspend fun searchDrinks(
        query: String,
        filters: Map<String, List<String>>
    ): List<Drink> {
        val searchRequest = filters
            .filter { it.key.isNotBlank() && it.value.isNotEmpty() }
            .map { it.key + "/" + it.value.joinToString(",") }
            .joinToString(separator = "/", prefix = query.takeIf { it.isNotBlank() }?.let { "search/$it/" } ?: "")

        return api.searchDrinks(searchRequest)
    }

    suspend fun getAggregation(): Aggregation = api.getAggregation()

    fun getFavoriteDrinks(): Flow<List<Drink>> = localStore.getFavoriteDrinks()
        .map { api.drinksByAliases(it.toList()) }

    @OptIn(ExperimentalCoroutinesApi::class)
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
}