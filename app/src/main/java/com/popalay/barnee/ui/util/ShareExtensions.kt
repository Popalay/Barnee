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
