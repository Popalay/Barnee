package com.popalay.barnee.data.remote

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationResponse
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.DrinksResponse
import com.popalay.barnee.data.model.FullDrinkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel.ALL
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType.Application
import kotlinx.serialization.json.Json

class Api(json: Json) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
            accept(Application.Json)
        }
        install(Logging) {
            level = ALL
        }
    }

    suspend fun drinks(query: String, skip: Int, take: Int): List<Drink> = try {
        client.get<DrinksResponse>("${baseUrl}drinks/$query?exactmatch=true&skip=$skip&take=$take").result
    } catch (e: NoTransformationFoundException) {
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
    } catch (e: NoTransformationFoundException) {
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
