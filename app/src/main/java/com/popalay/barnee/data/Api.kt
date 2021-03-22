package com.popalay.barnee.data

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.Receipt
import com.popalay.barnee.data.model.Response
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

object Api {
    private const val baseUrl = "https://api.absolutdrinks.com/drinks/"

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(Android) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
            accept(ContentType.Text.Html)
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

    suspend fun getReceipt(cocktail: String): Receipt {
        val document = withContext(Dispatchers.IO) {
            Jsoup.connect("https://www.absolutdrinks.com/en/drinks/$cocktail").get()
        }

        val scripts = document.head().select("script[type=application/ld+json]")
        val data = scripts[0].childNode(0).toString()
        return json.decodeFromString(data)
    }
}