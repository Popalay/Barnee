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

package com.popalay.barnee.ui.extensions

import androidx.compose.runtime.saveable.listSaver
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorSaver
import io.matthewnelson.component.parcelize.Parcelable

@OptIn(ExperimentalVoyagerApi::class, InternalVoyagerApi::class)
internal fun parcelableNavigatorSaver(): NavigatorSaver<Any> = NavigatorSaver { _, key, stateHolder, disposeBehavior, parent ->
    listSaver(
        save = { navigator ->
            val screenAsParcelables = navigator.items.filterIsInstance<Parcelable>()

            if (navigator.items.size > screenAsParcelables.size) {
                val screensNotParcelable = navigator.items.filterNot { screen -> screenAsParcelables.any { screen == it } }
                    .map { it::class.simpleName }
                    .joinToString()

                throw IllegalStateException("Unable to save instance state for Screens: $screensNotParcelable. " +
                        "Implement io.matthewnelson.component.parcelize.Parcelable on your Screen.")
            }

            screenAsParcelables
        },
        restore = { items ->
            @Suppress("UNCHECKED_CAST")
            Navigator(items as List<Screen>, key, stateHolder, disposeBehavior, parent)
        }
    )
}
