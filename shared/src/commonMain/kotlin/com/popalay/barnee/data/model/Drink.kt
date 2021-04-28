package com.popalay.barnee.data.model

import com.popalay.barnee.util.toIntIfInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Drink(
    val id: String,
    private val name: String,
    val alias: String,
    private val rating: Int,
    val images: List<Image>,
    private val story: String = "",
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