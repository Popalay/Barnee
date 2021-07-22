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

import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.filter
import com.kuuurt.paging.multiplatform.map
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.remote.Api
import com.popalay.barnee.data.repository.DrinksRequest.ByAliases
import com.popalay.barnee.data.repository.DrinksRequest.Collection
import com.popalay.barnee.data.repository.DrinksRequest.ForQuery
import com.popalay.barnee.data.repository.DrinksRequest.ForTags
import com.popalay.barnee.data.repository.DrinksRequest.Random
import com.popalay.barnee.data.repository.DrinksRequest.RelatedTo
import com.popalay.barnee.data.repository.DrinksRequest.Search
import com.popalay.barnee.util.filter
import com.popalay.barnee.util.toImageUrl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.take

interface DrinkRepository {
    fun drinks(request: DrinksRequest): Flow<PagingData<Drink>>
    fun randomDrink(): Flow<Drink>
    fun fullDrink(alias: String): Flow<FullDrinkResponse>
    fun aggregation(): Flow<Aggregation>
    fun categories(): Flow<List<Category>>
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class DrinkRepositoryImpl(
    private val api: Api,
    private val collectionRepository: CollectionRepository
) : DrinkRepository {
    override fun drinks(request: DrinksRequest): Flow<PagingData<Drink>> =
        when (request) {
            is RelatedTo -> requestPage { api.similarDrinks(request.alias) }
            is ForTags -> requestPage { api.drinksByTags(request.tags, it.skip, it.take) }
            is ForQuery -> requestPage { api.drinks(request.query, it.skip, it.take) }
            is ByAliases -> requestPage { api.drinksByAliases(request.aliases, it.skip, it.take) }
            is Random -> requestPage { api.random(it.skip, it.take) }
            is Search -> requestPage { searchDrinks(request.query, request.filters, it) }
            is Collection -> collection(request.name)
        }
            .distinctUntilChanged()

    override fun randomDrink(): Flow<Drink> = flow { emit(api.random(skip = 0, take = 1).first()) }
        .flatMapLatest { drink ->
            collectionRepository.collections()
                .mapLatest { collections -> drink.copy(userCollections = collections.filter(drink)) }
        }
        .distinctUntilChanged()

    override fun fullDrink(alias: String): Flow<FullDrinkResponse> = flow { emit(api.getFullDrink(alias)) }
        .flatMapLatest { response ->
            collectionRepository.collections()
                .mapLatest { collections ->
                    response.copy(
                        relatedDrinks = response.relatedDrinks.map { it.copy(userCollections = collections.filter(it)) },
                        drink = response.drink.copy(userCollections = collections.filter(response.drink))
                    )
                }
        }
        .distinctUntilChanged()

    override fun aggregation(): Flow<Aggregation> = flow { emit(api.getAggregation()) }

    override fun categories(): Flow<List<Category>> = flowOf(
        listOf(
            Category(
                text = "Non-alcoholic",
                alias = "is/not/alcoholic",
                imageUrl = "v1618923872/categories/non-alcoholic.webp".toImageUrl()
            ),
            Category(
                text = "The most famous",
                alias = "story/the famous",
                imageUrl = "v1618925730/categories/most-famous.webp".toImageUrl()
            ),
            Category(
                text = "Top rated",
                alias = "rating/80",
                imageUrl = "v1618925107/categories/top-rated.jpg".toImageUrl()
            ),
            Category(
                text = "Easy to do",
                alias = "skill/easy",
                imageUrl = "v1618923610/categories/easy-to-do.webp".toImageUrl()
            ),
            Category(
                text = "Hot",
                alias = "hot",
                imageUrl = "v1618923901/categories/hot.webp".toImageUrl()
            ),
            Category(
                text = "Cold",
                alias = "not/hot",
                imageUrl = "v1618925541/categories/cold.webp".toImageUrl()
            ),
            Category(
                text = "Carbonated",
                alias = "is/carbonated",
                imageUrl = "v1618923638/categories/carbonated.webp".toImageUrl()
            ),
            Category(
                text = "Non-carbonated",
                alias = "not/carbonated",
                imageUrl = "v1618923780/categories/non-carbonated.webp".toImageUrl()
            )
        )
    )

    private fun requestPage(request: suspend (PageRequest) -> List<Drink>): Flow<PagingData<Drink>> =
        DrinkPager(request).pages
            .flatMapLatest { pagedDrinks ->
                collectionRepository.collections()
                    .map { collections -> pagedDrinks.map { it.copy(userCollections = collections.filter(it)) } }
            }

    private fun collection(name: String): Flow<PagingData<Drink>> = collectionRepository.collection(name)
        .take(1)
        .flatMapLatest { collection ->
            DrinkPager { api.drinksByAliases(collection.aliases, it.skip, it.take) }.pages
                .flatMapLatest { pagedDrinks ->
                    collectionRepository.collection(name)
                        .flatMapLatest { newCollection ->
                            (if (collection.aliases.containsAll(newCollection.aliases)) {
                                flowOf(pagedDrinks.filter { it.alias in newCollection.aliases })
                            } else {
                                DrinkPager { api.drinksByAliases(newCollection.aliases, it.skip, it.take) }.pages
                            })
                                .map { drinks -> drinks.map { it.copy(userCollections = listOf(newCollection)) } }
                        }
                }
        }

    private suspend fun searchDrinks(
        query: String,
        filters: Map<String, List<String>>,
        pageRequest: PageRequest
    ): List<Drink> {
        val searchRequest = filters
            .filter { it.key.isNotBlank() && it.value.isNotEmpty() }
            .map { it.key + "/" + it.value.joinToString(",") }
            .joinToString(separator = "/", prefix = query.takeIf { it.isNotBlank() }?.let { "search/$it/" } ?: "")

        return api.searchDrinks(searchRequest, pageRequest.skip, pageRequest.take)
    }
}
