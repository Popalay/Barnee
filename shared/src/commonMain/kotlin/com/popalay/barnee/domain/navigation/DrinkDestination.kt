/*
 * Copyright (c) 2023 Denys Nykyforov
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

package com.popalay.barnee.domain.navigation

import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.identifier
import kotlin.jvm.JvmInline

@JvmInline
value class DrinkDestination private constructor(
    override val destination: String
) : Destination {
    constructor(
        identifier: String,
        name: String,
        image: ImageUrl
    ) : this("drink/$identifier?$KEY_IMAGE=$image&$KEY_NAME=$name")

    constructor(drink: Drink) : this(drink.identifier, drink.displayName, drink.displayImageUrl)

    companion object : RouteProvider {
        const val KEY_IDENTIFIER = "identifier"
        const val KEY_NAME = "name"
        const val KEY_IMAGE = "image"

        override val route: String = "drink/{$KEY_IDENTIFIER}?$KEY_IMAGE={$KEY_IMAGE}&$KEY_NAME={$KEY_NAME}"
    }
}
