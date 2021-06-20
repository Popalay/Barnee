package com.popalay.barnee.data.model

import kotlin.jvm.JvmInline

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
    override fun scaledUrl(size: Pair<Int, Int>): String =
        url + "?imwidth=${size.first}"

    override fun toString(): String = url
}

object EmptyImageUrl : ImageUrl {
    override val url: String = ""
    override fun scaledUrl(size: Pair<Int, Int>): String = ""
    override fun toString(): String = ""
}

fun String.toImageUrl() = when {
    isBlank() -> EmptyImageUrl
    startsWith("http") || startsWith("https") -> ExternalImageUrl(this)
    else -> InternalImageUrl(this)
}
