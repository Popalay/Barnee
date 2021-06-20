package com.popalay.barnee.ui.util

import androidx.compose.ui.unit.IntSize
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.popalay.barnee.data.model.ImageUrl

fun ImageRequest.Builder.applyForImageUrl(data: ImageUrl, size: IntSize) =
    data(data.scaledUrl(size.width to size.height))
        .memoryCacheKey(data.url)
        .placeholderMemoryCacheKey(MemoryCache.Key(data.url))
        .crossfade(true)
