package com.popalay.barnee.data.model

import com.popalay.barnee.util.toIntIfInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class FullDrinkResponse(
    val relatedDrinks: List<Drink>,
    val result: List<Drink>
) {
    @Transient
    val drink = result.first()
}

@Serializable
data class Drink(
    val id: String,
    val name: String,
    val alias: String,
    val rating: Int,
    val images: List<Image>,
    val story: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val categories: List<Category> = emptyList(),
    val occasions: List<Category> = emptyList(),
    val collections: List<Category> = emptyList(),
    val videos: List<Video> = emptyList(),
    @SerialName("nutritions") val nutrition: Nutrition = Nutrition(0),
    @SerialName("howToMix") val instruction: Instruction = Instruction(emptyList()),
    val isFavorite: Boolean = false
) {
    @Transient
    val displayImageUrl = images.lastOrNull()?.uri.orEmpty()

    @Transient
    val displayName = name.toLowerCase().removePrefix("absolut ")

    @Transient
    val videoUrl = videos.firstOrNull()?.youtube?.let { "https://www.youtube.com/watch?v=$it" }

    @Transient
    val displayRating = (rating / 10F).toIntIfInt().toString()

    @Transient
    val keywords = (categories + collections + occasions)
        .filter { it.alias.isNotBlank() }
        .map { it.text.toLowerCase() }
        .filter { it != "unknown" }

    @Transient
    val displayStory = story.trim().removeSuffix(".")
}

@Serializable
data class Ingredient(
    val text: String,
    val name: String,
    val nutrition: Int = 0
)

@Serializable
data class Instruction(
    @SerialName("stepByStep") val steps: List<InstructionStep>
)

val regex by lazy { "[\\[][\\w ,'-]+[|][\\w]+[|][\\w\\W]{8}-[\\w\\W]{4}-[\\w\\W]{4}-[\\w\\W]{4}-[\\w\\W]{12}+[]]".toRegex() }

@Serializable
data class InstructionStep(
    val action: String,
    val container: String,
    val text: String,
    val textReference: String,
    val milliliters: Double = 0.0
) {
    @Transient
    val displayText = text.replace(regex) {
        it.groups.firstOrNull()?.value.orEmpty().substringAfter("[").substringBefore("|")
    }.removeSuffix(".") + milliliters.takeIf { it > 0 }?.let { " (${it.toIntIfInt()}ml)" }.orEmpty()
}

@Serializable
data class Category(
    val text: String,
    val alias: String = "",
    val imageUrl: String = ""
)