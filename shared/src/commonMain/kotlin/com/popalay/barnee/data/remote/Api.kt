package com.popalay.barnee.data.remote

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationResponse
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.DrinksResponse
import com.popalay.barnee.data.model.FullDrinkResponse
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel.ALL
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType.Text
import kotlinx.serialization.json.Json

class Api(json: Json) {
    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
            accept(Text.Html)
        }
        install(Logging) {
            level = ALL
        }
    }

    suspend fun randomDrinks(count: Int): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}drinks/random/is/specificImage/InEnvironment?take=$count").result

    suspend fun drinksByAliases(aliases: List<String>): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}drinks/is/specificImage/InEnvironment/alias/${aliases.joinToString(",")}?exactmatch=true&take=100").result

    suspend fun drinksByTags(tags: List<String>): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}drinks/is/specificImage/InEnvironment/tag/${tags.joinToString(",")}?exactmatch=true&take=100").result

    suspend fun similarDrinks(alias: String): List<Drink> = getFullDrink(alias).relatedDrinks

    suspend fun searchDrinks(query: String): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}drinks/$query/is/specificImage/InEnvironment?take=100").result

    suspend fun getAggregation(): Aggregation =
        client.get<AggregationResponse>("${baseUrl}drinks/aggregations").metaData.aggregations

    suspend fun getFullDrink(alias: String): FullDrinkResponse =
        client.get("${baseUrl}drink/$alias?size=full&includerelateddrinks=true")

    companion object {
        private const val baseUrl = "https://api.absolutdrinks.com/"
    }
}