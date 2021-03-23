package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Response(
    val result: List<Drink>
)

@Serializable
data class Drink(
    val recipeId: String,
    val name: String,
    val alias: String,
    val rating: Int,
    val images: List<Image>
) {
    @Transient
    val displayImageUrl = images.last().uri
}

@Serializable
data class Image(
    val uri: String
)

@Serializable
data class Receipt(
    val name: String,
    val description: String,
    val keywords: String,
    val recipeIngredient: List<String>,
    val recipeInstructions: List<InstructionStep>,
    val nutrition: Nutrition,
    val video: List<Video> = emptyList(),
    val isFavorite: Boolean = false
) {
    @Transient
    val videoUrl = video.firstOrNull()?.contentUrl.orEmpty()

    @Transient
    val keywordsArray = keywords.split(",").map { it.trim() }
}

@Serializable
data class InstructionStep(
    val text: String
)

@Serializable
data class Video(
    val name: String,
    val description: String,
    val thumbnailUrl: String,
    val contentUrl: String,
)

@Serializable
data class Nutrition(
    val calories: String
)