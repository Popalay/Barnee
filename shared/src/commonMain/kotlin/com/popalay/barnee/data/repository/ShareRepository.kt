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

package com.popalay.barnee.data.repository

import com.popalay.barnee.data.device.Sharer
import com.popalay.barnee.data.remote.DeeplinkFactory
import com.popalay.barnee.util.capitalizeFirstChar

interface ShareRepository {
    suspend fun shareDrink(alias: String, displayName: String)
    suspend fun shareCollection(displayName: String)
}

internal class ShareRepositoryImpl(
    private val sharer: Sharer,
    private val deeplinkFactory: DeeplinkFactory
) : ShareRepository {
    override suspend fun shareDrink(alias: String, displayName: String) {
        val text = "Check out how to make a ${displayName.capitalizeFirstChar()}"
        val title = "Share drink"
        val shortUrl = deeplinkFactory.build("/drink/${alias}")

        sharer.openShareDialog(
            title = title,
            text = text,
            content = "$text $shortUrl"
        )
    }

    override suspend fun shareCollection(displayName: String) {
        val text = "Check out my collection - ${displayName.capitalizeFirstChar()}"
        val title = "Share collection"
        val shortUrl = deeplinkFactory.build("/collection/${displayName}")

        sharer.openShareDialog(
            title = title,
            text = displayName,
            content = "$text $shortUrl"
        )
    }
}
