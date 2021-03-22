package com.popalay.barnee.data.remote

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.Receipt
import com.popalay.barnee.data.model.Response
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object Api {
    private const val baseUrl = "https://api.absolutdrinks.com/drinks/"

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val client = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
            accept(Text.Html)
        }
        install(Logging)
    }

    suspend fun randomDrinks(count: Int): List<Drink> =
        client.get<Response>("${baseUrl}random/is/specificImage/InEnvironment?take=$count").result

    suspend fun drinksByAliases(aliases: List<String>): List<Drink> =
        client.get<Response>("${baseUrl}is/specificImage/InEnvironment/alias/${aliases.joinToString(",")}?exactmatch=true&take=100").result

    suspend fun drinksByTags(tags: List<String>): List<Drink> =
        client.get<Response>("${baseUrl}is/specificImage/InEnvironment/tag/${tags.joinToString(",")}?exactmatch=true&take=100").result

    suspend fun similarDrinks(alias: String): List<Drink> =
        client.get<Response>("${baseUrl}like/${alias}&take=100").result

    suspend fun searchDrinks(query: String): List<Drink> =
        client.get<Response>("${baseUrl}search/$query/is/specificImage/InEnvironment").result

    suspend fun getReceipt(cocktail: String): Receipt = withContext(Dispatchers.Default) {
        val data = HtmlExtractor.extract(
            url = "https://www.absolutdrinks.com/en/drinks/$cocktail",
            selector = "script[type=application/ld+json]"
        )
        json.decodeFromString(data)
    }
}