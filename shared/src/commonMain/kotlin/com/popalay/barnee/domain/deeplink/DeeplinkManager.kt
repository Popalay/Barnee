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

import co.touchlab.kermit.Logger
import com.eygraber.uri.Url
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.StackChange
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DeeplinkManager(
    private val router: Router,
    private val deeplinkHandlers: Set<DeeplinkHandler>,
) {

    @OptIn(DelicateCoroutinesApi::class)
    fun handle(deeplink: Url) {
        Logger.i(tag = "DeeplinkManager") { "Deeplink received: $deeplink" }
        deeplinkHandlers.firstOrNull { it.supports(deeplink) }?.createDestination(deeplink)?.let { destinations ->
            Logger.i(tag = "DeeplinkManager") { "Deeplink handled: $deeplink" }
            GlobalScope.launch {
                router.updateStack(StackChange.ReplaceAll(destinations))
            }
        } ?: run {
            Logger.i(tag = "DeeplinkManager") { "Deeplink not handled: $deeplink" }
        }
    }
}
