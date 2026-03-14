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

package com.popalay.barnee.data.remote

import com.popalay.barnee.data.model.Category
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.ExternalImageUrl
import com.popalay.barnee.data.model.Image
import com.popalay.barnee.data.model.Ingredient
import com.popalay.barnee.data.model.Instruction
import com.popalay.barnee.data.model.InstructionStep
import com.popalay.barnee.data.model.Nutrition
import com.popalay.barnee.data.remote.model.CocktailDbDrink

internal fun CocktailDbDrink.toDrink(): Drink = Drink(
    id = id,
    alias = alias,
    name = name,
    ingredients = ingredients.map { Ingredient(text = it.text) },
    instruction = Instruction(steps = steps.map { InstructionStep(it) }),
    images = listOfNotNull(imageUrl?.takeIf { it.isNotBlank() }?.let { Image(uri = ExternalImageUrl(it)) }),
    categories = categories.map { Category(text = it, alias = "category/${it.replace(' ', '_')}") },
    occasions = tags.map { Category(text = it, alias = "tag/$it") },
    collections = listOfNotNull(ibaCategory?.let { Category(text = it, alias = "iba/$it") }),
    rating = 0,
    nutrition = Nutrition(0),
)
