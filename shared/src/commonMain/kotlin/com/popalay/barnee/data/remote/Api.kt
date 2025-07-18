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

package com.popalay.barnee.data.remote

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationResponse
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.DrinksResponse
import com.popalay.barnee.data.model.FullDrinkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.request.get

class Api(private val client: HttpClient) {
    suspend fun drinks(query: String, skip: Int, take: Int): List<Drink> = try {
        client.get("${baseUrl}drinks/$query?exactmatch=true&skip=$skip&take=$take")
            .body<DrinksResponse>().result
    } catch (ignore: NoTransformationFoundException) {
        emptyList()
    }

    suspend fun drinksByAliases(aliases: Set<String>, skip: Int, take: Int): List<Drink> =
        drinks("alias/${aliases.joinToString(",")}", skip, take)

    suspend fun drinksByTags(tags: Set<String>, skip: Int, take: Int): List<Drink> =
        drinks("tag/${tags.joinToString(",")}", skip, take)

    suspend fun random(skip: Int, take: Int): List<Drink> =
        drinks("random/is/specificImage/InEnvironment", skip, take)

    suspend fun searchDrinks(query: String, skip: Int, take: Int): List<Drink> = try {
        client.get("${baseUrl}drinks/$query/is/specificImage/InEnvironment?skip=$skip&take=${take}")
            .body<DrinksResponse>().result
    } catch (ignore: NoTransformationFoundException) {
        emptyList()
    }

    suspend fun similarDrinks(alias: String): List<Drink> = getFullDrink(alias).relatedDrinks

    suspend fun getAggregation(): Aggregation =
        client.get("${baseUrl}drinks/aggregations").body<AggregationResponse>().metaData.aggregations

    suspend fun getFullDrink(alias: String): FullDrinkResponse =
        client.get("${baseUrl}drink/$alias?size=full&includerelateddrinks=true").body()

    companion object {
        private const val baseUrl = "https://api.absolutdrinks.com/"
    }
}
