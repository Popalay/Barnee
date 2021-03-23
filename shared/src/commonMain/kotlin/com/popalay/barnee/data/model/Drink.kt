package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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