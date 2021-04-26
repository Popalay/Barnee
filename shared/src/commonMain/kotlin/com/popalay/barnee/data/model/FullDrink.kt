package com.popalay.barnee.data.model

import com.popalay.barnee.util.toIntIfInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class FullDrinkResponse(
    val relatedDrinks: List<Drink>,
    val result: List<FullDrink>
) {
    @Transient
    val drink = result.first()
}

@Serializable
data class FullDrink(
    val id: String,
    val name: String,
    val alias: String,
    val rating: Int,
    val story: String,
    val images: List<Image>,
    val ingredients: List<Ingredient>,
    val categories: List<Category>,
    val occasions: List<Category>,
    val collections: List<Category>,
    val videos: List<Video> = emptyList(),
    @SerialName("nutritions") val nutrition: Nutrition,
    @SerialName("howToMix") val instruction: Instruction,
    val isFavorite: Boolean = false
) {
    @Transient
    val displayImageUrl = images.lastOrNull()?.uri.orEmpty()

    @Transient
    val displayName = name.toLowerCase().removePrefix("absolut ")

    @Transient
    val videoUrl = videos.firstOrNull()?.youtube?.let { "https://www.youtube.com/watch?v=$it" }

    @Transient
    val displayRating = "${(rating / 10F).toIntIfInt()}/10"

    @Transient
    val keywords = (categories + collections + occasions)
        .filter { it.alias.isNotBlank() }
        .map { it.text.toLowerCase() }
        .filter { it != "unknown" }
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