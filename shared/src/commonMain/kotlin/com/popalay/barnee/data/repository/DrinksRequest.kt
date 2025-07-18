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

package com.popalay.barnee.data.repository

import io.matthewnelson.component.parcelize.Parcelable
import io.matthewnelson.component.parcelize.Parcelize

sealed class DrinksRequest : Parcelable {
    @Parcelize
    data class RelatedTo(val alias: String) : DrinksRequest()

    @Parcelize
    data class ForTags(val tags: Set<String>) : DrinksRequest()

    @Parcelize
    data class ByAliases(val aliases: Set<String>) : DrinksRequest()

    @Parcelize
    data class ForQuery(val query: String) : DrinksRequest()

    @Parcelize
    data class Collection(val name: String) : DrinksRequest()

    @Parcelize
    data class Search(
        val query: String,
        val filters: Map<String, List<String>>
    ) : DrinksRequest()

    @Parcelize
    object Random : DrinksRequest()

    @Parcelize
    object Generated : DrinksRequest()
}
