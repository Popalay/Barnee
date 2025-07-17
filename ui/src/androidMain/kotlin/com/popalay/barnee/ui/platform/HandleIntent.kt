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

package com.popalay.barnee.ui.platform

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.eygraber.uri.Url
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.popalay.barnee.domain.deeplink.DeeplinkManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject

@Composable
actual fun HandleDeeplink() {
    val context = LocalContext.current
    val deeplinkManager = koinInject<DeeplinkManager>()
    LaunchedEffect(Unit) {
        callbackFlow {
            val componentActivity = context as ComponentActivity
            val currentIntent = componentActivity.intent
            if (currentIntent?.data != null) {
                trySend(currentIntent)
            }
            val consumer: (Intent) -> Unit = { trySend(it) }
            componentActivity.addOnNewIntentListener(consumer)
            awaitClose { componentActivity.removeOnNewIntentListener(consumer) }
        }.collectLatest {
            Firebase.dynamicLinks.getDynamicLink(it).addOnSuccessListener { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.toString()?.let(Url.Companion::parse)?.let(deeplinkManager::handle)
            }
        }
    }
}
