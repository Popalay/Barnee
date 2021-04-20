package com.popalay.barnee.data.repository

import com.popalay.barnee.data.local.LocalStore
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.repository.DrinksRequest.Favorites
import com.popalay.barnee.data.repository.DrinksRequest.ForQuery
import com.popalay.barnee.data.repository.DrinksRequest.ForTags
import com.popalay.barnee.data.repository.DrinksRequest.RelatedTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class DrinkRepository(
    private val api: Api,
    private val localStore: LocalStore
) {
    suspend fun getDrinks(request: DrinksRequest): Flow<List<Drink>> {
        return when (request) {
            is RelatedTo -> mapFavorites(api.similarDrinks(request.alias))
            is ForTags -> mapFavorites(api.drinksByTags(request.tags))
            is ForQuery -> mapFavorites(api.drinks(request.query))
            Favorites -> getFavoriteDrinks()
        }
    }

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

    fun getCategories(): Flow<List<Category>> = flowOf(
        listOf(
            Category(
                text = "Non-alcoholic",
                alias = "is/not/alcoholic",
                imageUrl = "v1618923872/categories/non-alcoholic.webp"
            ),
            Category(
                text = "The most famous",
                alias = "story/the famous",
                imageUrl = "v1618925730/categories/most-famous.webp"
            ),
            Category(
                text = "Top rated",
                alias = "rating/80",
                imageUrl = "v1618925107/categories/top-rated.jpg"
            ),
            Category(
                text = "Easy to do",
                alias = "skill/easy",
                imageUrl = "v1618923610/categories/easy-to-do.webp"
            ),
            Category(
                text = "Hot",
                alias = "hot",
                imageUrl = "v1618923901/categories/hot.webp"
            ),
            Category(
                text = "Cold",
                alias = "not/hot",
                imageUrl = "v1618925541/categories/cold.webp"
            ),
            Category(
                text = "Carbonated",
                alias = "carbonated",
                imageUrl = "v1618923638/categories/carbonated.webp"
            ),
            Category(
                text = "Non-carbonated",
                alias = "not/carbonated",
                imageUrl = "v1618923780/categories/non-carbonated.webp"
            )
        )
    )

    private suspend fun saveAsFavorite(alias: String) {
        localStore.saveFavorite(alias)
    }

    private suspend fun removeFromFavorites(alias: String) {
        localStore.removeFavorite(alias)
    }

    private fun getFavoriteDrinks(): Flow<List<Drink>> = localStore.getFavoriteDrinks()
        .take(1)
        .map { favorites -> api.drinksByAliases(favorites) }
        .flatMapLatest { drinks ->
            localStore.getFavoriteDrinks()
                .map { favorites ->
                    drinks.filter { it.alias in favorites }
                        .let { if (it.size == favorites.size) it else api.drinksByAliases(favorites) }
                }
        }
        .map { drinks -> drinks.map { it.copy(isFavorite = true) } }

    private fun mapFavorites(drinks: List<Drink>) = localStore.getFavoriteDrinks()
        .map { favorites -> drinks.map { it.copy(isFavorite = it.alias in favorites) } }
}