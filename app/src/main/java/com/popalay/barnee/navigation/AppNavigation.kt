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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.popalay.barnee.domain.Input
import com.popalay.barnee.domain.navigation.Destination
import com.popalay.barnee.domain.navigation.RouteProvider
import com.popalay.barnee.navigation.NavigationCommand.NavigationType.BottomSheet
import com.popalay.barnee.navigation.NavigationCommand.NavigationType.Dialog
import com.popalay.barnee.navigation.NavigationCommand.NavigationType.Screen

const val DeeplinkPrefix = "https://barnee.com/"

fun NavController.navigate(destination: Destination) {
    navigate(destination.destination)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun <T : Input> NavGraphBuilder.navigationNode(
    command: NavigationCommand<T>,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    when (command.navigationType) {
        Dialog -> dialog(
            route = command.route,
            deepLinks = command.deeplinks,
            arguments = command.arguments,
            content = content
        )

        BottomSheet -> bottomSheet(
            route = command.route,
            deepLinks = command.deeplinks,
            arguments = command.arguments,
            content = {
                content(it)
            }
        )

        Screen -> composable(
            route = command.route,
            deepLinks = command.deeplinks,
            arguments = command.arguments,
            content = content
        )
    }
}

interface NavigationCommand<T : Input> : RouteProvider {
    val arguments: List<NamedNavArgument> get() = emptyList()
    val deeplinks: List<NavDeepLink> get() = emptyList()

    val navigationType: NavigationType get() = Screen

    fun parseInput(backStackEntry: NavBackStackEntry): T {
        throw UnsupportedOperationException("Input is not supported for ${this::class.simpleName}")
    }

    enum class NavigationType {
        Dialog, BottomSheet, Screen
    }
}
