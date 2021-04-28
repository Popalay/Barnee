package com.popalay.barnee.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val text: String,
    val alias: String = "",
    val imageUrl: String = ""
)