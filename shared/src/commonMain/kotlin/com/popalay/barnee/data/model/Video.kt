package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val name: String,
    val description: String,
    val thumbnailUrl: String,
    val contentUrl: String,
)