/*
 * Copyright (c) 2021 Denys Nykyforov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.popalay.barnee.util

import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.data.model.Category
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
        .filter { it.text != "unknown" }

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

val Category.displayText get() = text.lowercase()
