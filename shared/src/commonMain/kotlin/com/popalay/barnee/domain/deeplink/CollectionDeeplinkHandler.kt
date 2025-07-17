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

package com.popalay.barnee.domain.deeplink

import com.eygraber.uri.Url
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.TypedScreenProvider

class CollectionDeeplinkHandler : DeeplinkHandler {
    override fun supports(deeplink: Url): Boolean =
        deeplink.path?.startsWith("/collection/") == true

    override fun createDestination(deeplink: Url): List<TypedScreenProvider>? {
        val collection = Collection(
            name = deeplink.pathSegments.lastOrNull() ?: return null,
            aliases = deeplink.getQueryParameter("aliases")?.split(",")?.toSet() ?: return null,
        )
        return listOf(AppScreens.Discovery, AppScreens.SingleCollection(collection))
    }

    companion object {
        fun createDeeplink(collection: Collection): String =
            "collection/${collection.name}?aliases=${collection.aliases.joinToString(",")}"
    }
}
