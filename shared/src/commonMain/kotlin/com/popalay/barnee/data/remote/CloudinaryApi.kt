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

package com.popalay.barnee.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import io.ktor.util.sha1
import io.ktor.utils.io.core.toByteArray
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class CloudinaryResponse(@SerialName("secure_url") val secureUrl: String)

class CloudinaryApi(
    private val client: HttpClient,
    private val apiSecret: String,
) {

    companion object {
        private const val CLOUDINARY_API_URL = "https://api.cloudinary.com/v1_1/barnee/image/upload"
        private const val CLOUDINARY_RES_URL = "https://res.cloudinary.com/barnee/image/upload/"
    }

    suspend fun uploadImage(imageUrl: String): String {
        try {
            val apikey = "729651476793848"
            val timestamp = Clock.System.now().epochSeconds
            val signature = "timestamp=$timestamp$apiSecret".toSha1()
            return client.submitForm(
                url = CLOUDINARY_API_URL,
                formParameters = Parameters.build {
                    append("file", imageUrl)
                    append("api_key", apikey)
                    append("timestamp", timestamp.toString())
                    append("signature", signature)
                }
            ).body<CloudinaryResponse>().secureUrl.substringAfter(CLOUDINARY_RES_URL)
        } finally {
            client.close()
        }
    }
}

private fun String.toSha1(): String = sha1(this.toByteArray()).joinToString(separator = "") {
    val hexChars = "0123456789abcdef"
    val highNibble = (it.toInt() shr 4) and 0x0F
    val lowNibble = it.toInt() and 0x0F
    "${hexChars[highNibble]}${hexChars[lowNibble]}"
}
