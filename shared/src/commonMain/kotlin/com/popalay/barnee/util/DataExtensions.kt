package com.popalay.barnee.util

import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.EmptyImageUrl
import com.popalay.barnee.data.model.ExternalImageUrl
import com.popalay.barnee.data.model.InternalImageUrl

val Drink.displayImageUrl
    get() = images.lastOrNull()?.uri ?: EmptyImageUrl

val Drink.displayName
    get() = name.lowercase().removePrefix("absolut").trim()

val Drink.videoUrl
    get() = videos.firstOrNull()?.youtube?.let { "https://www.youtube.com/watch?v=$it" }

val Drink.displayRating
    get() = (rating / 10F).toIntIfInt().toString()

val Drink.keywords
    get() = (categories + collections + occasions)
        .filter { it.alias.isNotBlank() }
        .map { it.text.lowercase() }
        .filter { it != "unknown" }

val Drink.displayStory
    get() = story.trim().removeSuffix(".")

val Drink.inCollection
    get() = collection != null

fun Set<Collection>.with(drink: Drink) = firstOrNull { drink.alias in it.aliases }

val AggregationGroup.displayNames get() = values.keys.map { it.replace(' ', '-') to it.lowercase().replace('-', ' ') }

fun String.toImageUrl() = when {
    isBlank() -> EmptyImageUrl
    startsWith("http") || startsWith("https") -> ExternalImageUrl(this)
    else -> InternalImageUrl(this)
}

fun Collection.isEmpty() = aliases.isEmpty()

fun Collection.isNotEmpty() = !isEmpty()
