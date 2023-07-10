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

package com.popalay.barnee.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.StackChange
import com.popalay.barnee.domain.navigation.TypedScreenProvider
import com.popalay.barnee.ui.util.LifecycleAwareLaunchedEffect
import org.koin.compose.koinInject

@Composable
internal fun NavigationHost(
    navigator: Navigator,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val changeStackFlow = koinInject<Router>().stackChangeFlow

    LifecycleAwareLaunchedEffect(changeStackFlow) { stackChange ->
        when (stackChange) {
            is StackChange.Push -> when (stackChange.destination.type) {
                TypedScreenProvider.Type.FullScreen  -> navigator.push(ScreenRegistry.get(stackChange.destination))
                TypedScreenProvider.Type.BottomSheet -> bottomSheetNavigator.show(ScreenRegistry.get(stackChange.destination))
            }

            is StackChange.Replace -> when (stackChange.destination.type) {
                TypedScreenProvider.Type.FullScreen -> {
                    bottomSheetNavigator.hide()
                    navigator.replace(ScreenRegistry.get(stackChange.destination))
                }

                TypedScreenProvider.Type.BottomSheet -> bottomSheetNavigator.show(ScreenRegistry.get(stackChange.destination))
            }

            is StackChange.Pop     -> {
                if (bottomSheetNavigator.isVisible) {
                    bottomSheetNavigator.hide()
                } else {
                    navigator.pop()
                }
            }
        }
    }
}
