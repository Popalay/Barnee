package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Collection(
    val name: String,
    val aliases: Set<String>,
    val cover: Set<ImageUrl>
)
