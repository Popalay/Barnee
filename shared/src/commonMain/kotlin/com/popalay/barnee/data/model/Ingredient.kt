package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val text: String,
    val name: String,
    val nutrition: Int = 0
)
