/*
 * Copyright (c) 2025 Denys Nykyforov
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

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.FullDrinkResponse
import com.popalay.barnee.data.remote.toDrink
import com.popalay.barnee.data.remote.model.CocktailDbDrink
import com.popalay.barnee.util.readResource
import kotlinx.serialization.json.Json

internal class LocalDrinkDataSource(private val json: Json) {

    private val drinks: List<Drink> by lazy {
        json.decodeFromString<List<CocktailDbDrink>>(readResource("cocktails.json"))
            .map { it.toDrink() }
    }

    fun drinksByAliases(aliases: Set<String>, skip: Int, take: Int): List<Drink> =
        drinks.filter { it.alias in aliases }.paginate(skip, take)

    fun drinksByTags(tags: Set<String>, skip: Int, take: Int): List<Drink> {
        val normalised = tags.map { it.lowercase() }.toSet()
        return drinks.filter { drink ->
            drink.occasions.any { it.text.lowercase() in normalised } ||
                drink.categories.any { it.text.lowercase() in normalised }
        }.paginate(skip, take)
    }

    fun drinksByQuery(query: String, skip: Int, take: Int): List<Drink> {
        if (query.isBlank()) return drinks.paginate(skip, take)
        return when {
            query.startsWith("category/") -> {
                val value = query.removePrefix("category/").replace('_', ' ').lowercase()
                drinks.filter { drink -> drink.categories.any { it.text.lowercase() == value } }
            }
            query.startsWith("tag/") -> {
                val value = query.removePrefix("tag/").lowercase()
                drinks.filter { drink -> drink.occasions.any { it.text.lowercase() == value } }
            }
            query.startsWith("iba/") -> {
                val value = query.removePrefix("iba/").lowercase()
                drinks.filter { drink -> drink.collections.any { it.text.lowercase() == value } }
            }
            else -> filterByQuery(query, drinks)
        }.paginate(skip, take)
    }

    fun random(skip: Int, take: Int): List<Drink> =
        drinks.shuffled().paginate(skip, take)

    fun searchDrinks(query: String, filters: Map<String, List<String>>, skip: Int, take: Int): List<Drink> {
        var result = drinks

        if (query.isNotBlank()) {
            val q = query.lowercase()
            result = result.filter { drink ->
                drink.name.lowercase().contains(q) ||
                    drink.ingredients.any { it.text.lowercase().contains(q) }
            }
        }

        filters.forEach { (group, values) ->
            if (values.isEmpty()) return@forEach
            val normalised = values.map { it.lowercase().replace('-', ' ') }.toSet()
            result = result.filter { drink ->
                when (group) {
                    "withType"  -> drink.categories.take(1).any { it.text.lowercase() in normalised }
                    "servedIn"  -> drink.categories.drop(1).take(1).any { it.text.lowercase() in normalised }
                    "skill"     -> drink.categories.drop(2).take(1).any { it.text.lowercase() in normalised }
                    "tasting"   -> drink.occasions.any { it.text.lowercase() in normalised }
                    "colored"   -> drink.occasions.any { it.text.lowercase() in normalised }
                    else        -> true
                }
            }
        }

        return result.paginate(skip, take)
    }

    fun similarDrinks(alias: String): List<Drink> {
        val target = drinks.firstOrNull { it.alias == alias } ?: return emptyList()
        val targetCategories = target.categories.map { it.text }.toSet()
        return drinks
            .filter { it.alias != alias && it.categories.any { cat -> cat.text in targetCategories } }
            .take(MAX_RELATED)
    }

    fun getFullDrink(alias: String): FullDrinkResponse {
        val drink = drinks.first { it.alias == alias }
        return FullDrinkResponse(relatedDrinks = similarDrinks(alias), drink = drink)
    }

    fun getAggregation(): Aggregation = Aggregation(
        withType = aggregationGroupFor { drink ->
            // First category is always the drink type (Cocktail, Shot, Beer, etc.)
            drink.categories.take(1).map { it.text }
        },
        servedIn = aggregationGroupFor { drink ->
            // Second category is always the glass type
            drink.categories.drop(1).take(1).map { it.text }
        },
        skill = aggregationGroupFor { drink ->
            // Third category is always alcoholic classification
            drink.categories.drop(2).take(1).map { it.text }
        },
        tasting = aggregationGroupFor { drink ->
            drink.occasions.filter { it.text in TASTING_TAGS }.map { it.text }
        },
        colored = aggregationGroupFor { drink ->
            drink.occasions.filter { it.text in CHARACTER_TAGS }.map { it.text }
        },
    )

    private fun aggregationGroupFor(selector: (Drink) -> List<String>): AggregationGroup {
        val counts = mutableMapOf<String, Int>()
        drinks.forEach { drink ->
            selector(drink).forEach { value ->
                counts[value] = (counts[value] ?: 0) + 1
            }
        }
        return AggregationGroup(counts)
    }

    private fun List<Drink>.paginate(skip: Int, take: Int): List<Drink> =
        drop(skip).take(take)

    private fun filterByQuery(query: String, source: List<Drink>): List<Drink> {
        if (query.isBlank()) return source
        val q = query.lowercase()
        return source.filter { drink ->
            drink.name.lowercase().contains(q) ||
                drink.categories.any { it.text.lowercase().contains(q) } ||
                drink.occasions.any { it.text.lowercase().contains(q) } ||
                drink.ingredients.any { it.text.lowercase().contains(q) }
        }
    }

    companion object {
        private const val MAX_RELATED = 10

        private val TASTING_TAGS = setOf(
            "Sweet", "Sour", "Bitter", "Citrus", "Fruity", "Fresh", "Savory", "Sharp"
        )
        private val CHARACTER_TAGS = setOf(
            "Strong", "Mild", "Refreshing", "Bubbly", "Frozen", "Cold", "Dark", "Clear"
        )
    }
}
