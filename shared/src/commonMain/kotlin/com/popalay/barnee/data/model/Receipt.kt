package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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