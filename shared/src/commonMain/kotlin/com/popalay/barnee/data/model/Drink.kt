package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Drink(
    val id: String,
    val name: String,
    val alias: String,
    val rating: Int,
    val images: List<Image>,
    val isFavorite: Boolean = false
) {
    @Transient
    val displayImageUrl = images.last().uri

    @Transient
    val displayName = name.toLowerCase().removePrefix("absolut ")

    @Transient
    val displayRating = (rating / 10F).toString()
}