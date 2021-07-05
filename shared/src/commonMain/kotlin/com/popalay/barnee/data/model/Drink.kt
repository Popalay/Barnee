package com.popalay.barnee.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Drink(
    val id: String,
    internal val name: String,
    val alias: String,
    internal val rating: Int,
    internal val story: String = "",
    val images: List<Image> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val categories: List<Category> = emptyList(),
    val occasions: List<Category> = emptyList(),
    val collections: List<Category> = emptyList(),
    val videos: List<Video> = emptyList(),
    @SerialName("nutritions") val nutrition: Nutrition = Nutrition(0),
    @SerialName("howToMix") val instruction: Instruction = Instruction(emptyList()),
    val collection: Collection? = null
)
