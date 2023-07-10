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

package com.popalay.barnee.data.repository

import com.popalay.barnee.data.device.Sharer
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.data.remote.DeeplinkFactory
import com.popalay.barnee.util.capitalizeFirstChar
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.isGenerated

interface ShareRepository {
    suspend fun shareDrink(drink: Drink)
    suspend fun shareCollection(collection: Collection)
}

internal class ShareRepositoryImpl(
    private val sharer: Sharer,
    private val deeplinkFactory: DeeplinkFactory
) : ShareRepository {
    override suspend fun shareDrink(drink: Drink) {
        val title = "Share drink"
        val instruction = drink.instruction.steps
            .mapIndexed { index, step -> "\t${index + 1}. ${step.text}" }
            .joinToString("\n")
        val text = "Check out how to make a ${drink.displayName.capitalizeFirstChar()}:\n$instruction"
        val shortUrl = drink.takeIf { !it.isGenerated }
//            ?.let { deeplinkFactory.build(DrinkDestination(drink).destination) }.orEmpty() // TODO

        sharer.openShareDialog(
            title = title,
            text = text,
            content = "$text\n$shortUrl"
        )
    }

    override suspend fun shareCollection(collection: Collection) {
        val title = "Share collection"
        val drinks = collection.aliases
            .joinToString("\n") { "• ${it.replace('-', ' ').capitalizeFirstChar()}" }
        val text = "Check out my collection - ${collection.name.capitalizeFirstChar()}:\n$drinks"
        val shortUrl = ""//deeplinkFactory.build(CollectionDestination(collection).destination) //TODO

        sharer.openShareDialog(
            title = title,
            text = collection.name,
            content = "$text\n$shortUrl"
        )
    }
}
