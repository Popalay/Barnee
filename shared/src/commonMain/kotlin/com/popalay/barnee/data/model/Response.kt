package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val result: List<Drink>
)