package com.popalay.barnee.data.model

import com.popalay.barnee.data.transformer.StringAsImageUrlTransformer
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @Serializable(with = StringAsImageUrlTransformer::class)
    val uri: ImageUrl
)