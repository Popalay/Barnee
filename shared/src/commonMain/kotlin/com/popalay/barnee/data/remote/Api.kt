package com.popalay.barnee.data.remote

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationResponse
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.DrinksResponse
import com.popalay.barnee.data.model.FullDrinkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.request.get

class Api(private val client: HttpClient) {
    suspend fun drinks(query: String, skip: Int, take: Int): List<Drink> = try {
        client.get<DrinksResponse>("${baseUrl}drinks/$query?exactmatch=true&skip=$skip&take=$take").result
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
        client.get<DrinksResponse>("${baseUrl}drinks/$query/is/specificImage/InEnvironment?skip=$skip&take=${take}").result
    } catch (ignore: NoTransformationFoundException) {
        emptyList()
    }

    suspend fun similarDrinks(alias: String): List<Drink> = getFullDrink(alias).relatedDrinks

    suspend fun getAggregation(): Aggregation =
        client.get<AggregationResponse>("${baseUrl}drinks/aggregations").metaData.aggregations

    suspend fun getFullDrink(alias: String): FullDrinkResponse =
        client.get("${baseUrl}drink/$alias?size=full&includerelateddrinks=true")

    companion object {
        private const val baseUrl = "https://api.absolutdrinks.com/"
    }
}
