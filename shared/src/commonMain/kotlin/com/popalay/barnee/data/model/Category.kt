package com.popalay.barnee.data.model

import com.popalay.barnee.data.transformer.StringAsImageUrlTransformer
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val text: String,
    val alias: String = "",
    @Serializable(with = StringAsImageUrlTransformer::class)
    val imageUrl: ImageUrl = EmptyImageUrl
)
