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

package com.popalay.barnee.data.model

import com.popalay.barnee.data.transformer.SanitizeStringTransformer
import com.popalay.barnee.domain.Input
import io.matthewnelson.component.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Drink(
    val id: String = "",
    val alias: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val userCollections: List<Collection> = emptyList(),
    @SerialName("howToMix") val instruction: Instruction = Instruction(emptyList()),
    @Serializable(with = SanitizeStringTransformer::class) internal val name: String,
    internal val rating: Int = 0,
    @Serializable(with = SanitizeStringTransformer::class) internal val story: String = "",
    internal val images: List<Image> = emptyList(),
    internal val categories: List<Category> = emptyList(),
    internal val occasions: List<Category> = emptyList(),
    internal val collections: List<Category> = emptyList(),
    internal val videos: List<Video> = emptyList(),
    @SerialName("nutritions") internal val nutrition: Nutrition = Nutrition(0)
)

@Parcelize
data class DrinkMinimumData(
    val identifier: String,
    val name: String,
    val displayImageUrl: ImageUrl,
) : Input
