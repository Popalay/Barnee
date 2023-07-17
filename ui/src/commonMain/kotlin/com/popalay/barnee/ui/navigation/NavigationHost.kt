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

package com.popalay.barnee.ui.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.StackChange
import com.popalay.barnee.domain.navigation.TypedScreenProvider
import com.popalay.barnee.ui.platform.LifecycleAwareLaunchedEffect
import org.koin.compose.koinInject

@Composable
fun NavigationHost(
    navigator: Navigator,
    bottomSheetNavigator: BottomSheetNavigator,
) {
    val changeStackFlow = koinInject<Router>().stackChangeFlow

    LifecycleAwareLaunchedEffect(changeStackFlow) { stackChange ->
        when (stackChange) {
            is StackChange.Push       -> {
                if (stackChange.destinations.size == 1) {
                    val destination = stackChange.destinations.first()
                    when (destination.type) {
                        TypedScreenProvider.Type.FullScreen  -> {
                            if (bottomSheetNavigator.isVisible) {
                                bottomSheetNavigator.hide()
                            }
                            navigator.push(ScreenRegistry.get(destination))
                        }

                        TypedScreenProvider.Type.BottomSheet -> bottomSheetNavigator.show(ScreenRegistry.get(destination))
                    }
                } else {
                    navigator.push(stackChange.destinations.map(ScreenRegistry::get))
                }
            }

            is StackChange.Replace    -> {
                val destination = stackChange.destination
                when (destination.type) {
                    TypedScreenProvider.Type.FullScreen  -> {
                        if (bottomSheetNavigator.isVisible) {
                            bottomSheetNavigator.hide()
                            navigator.push(ScreenRegistry.get(destination))
                        } else {
                            navigator.replace(ScreenRegistry.get(destination))
                        }
                    }

                    TypedScreenProvider.Type.BottomSheet -> bottomSheetNavigator.show(ScreenRegistry.get(destination))
                }
            }

            is StackChange.ReplaceAll -> {
                if (stackChange.destinations.size == 1) {
                    val destination = stackChange.destinations.first()
                    when (destination.type) {
                        TypedScreenProvider.Type.FullScreen  -> {
                            bottomSheetNavigator.hide()
                            navigator.replaceAll(ScreenRegistry.get(destination))
                        }

                        TypedScreenProvider.Type.BottomSheet -> bottomSheetNavigator.show(ScreenRegistry.get(destination))
                    }
                } else {
                    navigator.replaceAll(stackChange.destinations.map(ScreenRegistry::get))
                }
            }

            is StackChange.Pop        -> {
                if (bottomSheetNavigator.isVisible) {
                    bottomSheetNavigator.hide()
                } else {
                    navigator.pop()
                }
            }
        }
    }
}
