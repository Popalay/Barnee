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

import android.net.Uri
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal actual class DeeplinkFactoryImpl : DeeplinkFactory {
    override suspend fun build(suffix: String): String = suspendCoroutine { continuation ->
        val googlePlayLink = Uri.parse("https://play.google.com/store/apps/details?id=com.popalay.barnee")
        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            this.link = Uri.parse(DeeplinkFactory.DEEPLINK_URI_PREFIX + suffix)
            this.domainUriPrefix = DeeplinkFactory.DOMAIN_URI_PREFIX
            androidParameters {
                this.fallbackUrl = googlePlayLink
            }
        }
            .addOnSuccessListener { continuation.resume(it.shortLink.toString()) }
            .addOnFailureListener {
                val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                    this.link = link
                    this.domainUriPrefix = domainUriPrefix
                    androidParameters {
                        this.fallbackUrl = fallbackUrl
                    }
                }
                continuation.resume(dynamicLink.uri.toString())
            }
    }
}
