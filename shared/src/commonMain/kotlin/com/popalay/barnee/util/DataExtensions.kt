/*
 * Copyright (c) 2025 Denys Nykyforov
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
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.data.model.EmptyImageUrl
import com.popalay.barnee.data.model.ExternalImageUrl
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.data.model.InternalImageUrl

val Drink.displayImageUrl: ImageUrl
    get() = images.firstOrNull { it.specificImage == "InEnvironment" }?.uri
        ?: images.firstOrNull { it.specificImage == "Black" }?.uri
        ?: images.lastOrNull()?.uri
        ?: EmptyImageUrl

val Drink.displayName: String get() = name.lowercase().removePrefix("absolut").trim()

val Drink.videoId: String? get() = videos.firstOrNull()?.youtube

val Drink.displayRating: String get() = (rating / 10F).toIntIfInt().toString()

val Drink.displayRatingWithMax: String get() = displayRating.let { "$it/10" }

val Drink.isGenerated: Boolean get() = id.startsWith("generated_")

val Drink.identifier: String get() = alias.ifBlank { id }

val Drink.keywords: List<Category>
    get() = (categories + collections + occasions)
        .filter { it.alias.isNotBlank() }
        .filter { it.text != "unknown" }

val Drink.displayStory: String get() = story.trim().removeSuffix(".")

val Drink.inCollections: Boolean get() = userCollections.isNotEmpty()

val Drink.calories: String get() = nutrition.totalCalories.toString().let { "$it kcal" }

val Drink.collection: Collection? get() = userCollections.firstOrNull()

fun Iterable<Collection>.filter(drink: DrinkMinimumData) = filter { drink.identifier in it.aliases }
fun Iterable<Collection>.filter(drink: Drink) = filter { drink.identifier in it.aliases }

val AggregationGroup.displayNames get() = values.keys.map { it.replace(' ', '-') to it.lowercase().replace('-', ' ') }

fun String.toImageUrl() = when {
    isBlank()                                 -> EmptyImageUrl
    startsWith("http") || startsWith("https") -> ExternalImageUrl(this)
    else                                      -> InternalImageUrl(this)
}

fun Drink.toMinimumData() = DrinkMinimumData(
    identifier = identifier,
    name = displayName,
    displayImageUrl = displayImageUrl
)

val Category.displayText get() = text.lowercase()

val Collection.isDefault get() = name == Collection.DEFAULT_NAME
