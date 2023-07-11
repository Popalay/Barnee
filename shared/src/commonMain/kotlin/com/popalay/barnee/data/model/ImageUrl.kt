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

package com.popalay.barnee.data.model

import com.popalay.barnee.data.transformer.StringAsImageUrlTransformer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable(with = StringAsImageUrlTransformer::class)
interface ImageUrl {
    val url: String
    fun scaledUrl(size: Pair<Int, Int>): String
}

@JvmInline
value class InternalImageUrl(private val rawUrl: String) : ImageUrl {
    override val url: String get() = "${URL_PREFIX}$rawUrl"

    override fun scaledUrl(size: Pair<Int, Int>): String =
        "${URL_PREFIX}c_scale,w_${size.first}/$rawUrl"

    override fun toString(): String = rawUrl

    companion object {
        private const val URL_PREFIX = "https://res.cloudinary.com/barnee/image/upload/"
    }
}

@JvmInline
value class ExternalImageUrl(override val url: String) : ImageUrl {

    override fun scaledUrl(size: Pair<Int, Int>): String {
        val separator = if (url.contains('?')) "&" else "?"
        return "$url${separator}imwidth=${size.first}"
    }

    override fun toString(): String = url
}

object EmptyImageUrl : ImageUrl {
    override val url: String = ""
    override fun scaledUrl(size: Pair<Int, Int>): String = ""
    override fun toString(): String = ""
}
