package com.popalay.barnee.data.model

import com.popalay.barnee.data.transformer.DrinkListFirstElementTransformer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullDrinkResponse(
    val relatedDrinks: List<Drink>,
    @Serializable(with = DrinkListFirstElementTransformer::class)
    @SerialName("result")
    val drink: Drink
)
