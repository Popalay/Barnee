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

package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink
import com.popalay.barnee.domain.collection.CollectionInput
import com.popalay.barnee.domain.navigation.CollectionDestination
import com.popalay.barnee.domain.navigation.CollectionDestination.Companion.KEY_NAME
import com.popalay.barnee.domain.navigation.RouteProvider

object CollectionNavigationCommand : NavigationCommand<CollectionInput>,
    RouteProvider by CollectionDestination.Companion {
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_NAME) { type = NavType.StringType },
    )

    override val deeplinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${DEEPLINK_PREFIX}$route" }
    )

    override fun parseInput(backStackEntry: NavBackStackEntry): CollectionInput {
        val name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
        return CollectionInput(name)
    }
}
