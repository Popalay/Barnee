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

package com.popalay.barnee.ui.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase

internal fun Context.shareDrink(displayName: String, alias: String) {
    val link = "https://barnee.com/drink/${alias}".toUri()
    val domainUriPrefix = "https://barnee.page.link"
    val googlePlayLink = "https://play.google.com/store/apps/details?id=com.popalay.barnee".toUri()
    val text = "Check out how to make a ${displayName.capitalizeFirstChar()}"
    val title = "Share drink"

    Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
        this.link = link
        this.domainUriPrefix = domainUriPrefix
        androidParameters {
            fallbackUrl = googlePlayLink
        }
    }
        .addOnSuccessListener { shortLink ->
            launchChooser(
                title = title,
                text = text,
                content = "$text ${shortLink.shortLink.toString()}"
            )
        }
        .addOnFailureListener {
            val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                this.link = link
                this.domainUriPrefix = domainUriPrefix
                androidParameters {
                    fallbackUrl = googlePlayLink
                }
            }
            launchChooser(
                title = title,
                text = text,
                content = "$text ${dynamicLink.uri}"
            )
        }
}

private fun Context.launchChooser(title: String, content: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, text)
        putExtra(Intent.EXTRA_TEXT, content)
    }
    startActivity(Intent.createChooser(intent, title))
}
