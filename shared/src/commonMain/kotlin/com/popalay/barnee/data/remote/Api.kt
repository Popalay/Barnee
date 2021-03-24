package com.popalay.barnee.data.remote

import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationResponse
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.DrinksResponse
import com.popalay.barnee.data.model.Receipt
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel.ALL
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// Caused by: kotlin.native.concurrent.InvalidMutabilityException:
// mutation attempt of frozen com.popalay.barnee.data.remote.Api
class Api(
    json: Json,
    private val htmlExtractor: HtmlExtractor
) {
    private val localJson = json

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
        client.get<DrinksResponse>("${baseUrl}random/is/specificImage/InEnvironment?take=$count").result

    suspend fun drinksByAliases(aliases: List<String>): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}is/specificImage/InEnvironment/alias/${aliases.joinToString(",")}?exactmatch=true&take=100").result

    suspend fun drinksByTags(tags: List<String>): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}is/specificImage/InEnvironment/tag/${tags.joinToString(",")}?exactmatch=true&take=100").result

    suspend fun similarDrinks(alias: String): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}like/${alias}&take=100").result

    suspend fun searchDrinks(query: String): List<Drink> =
        client.get<DrinksResponse>("${baseUrl}$query/is/specificImage/InEnvironment?take=100").result

    suspend fun getAggregation(): Aggregation =
        client.get<AggregationResponse>("${baseUrl}aggregations").metaData.aggregations

    suspend fun getReceipt(alias: String): Receipt = withContext(Dispatchers.Default) {
        val data = htmlExtractor.extract(
            url = "https://www.absolutdrinks.com/en/drinks/$alias",
            selector = "script[type=application/ld+json]"
        )
        localJson.decodeFromString(data)
    }

    companion object {
        private const val baseUrl = "https://api.absolutdrinks.com/drinks/"
    }
}