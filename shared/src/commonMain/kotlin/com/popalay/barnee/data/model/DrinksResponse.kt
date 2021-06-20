package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DrinksResponse(
    val result: List<Drink>
)
