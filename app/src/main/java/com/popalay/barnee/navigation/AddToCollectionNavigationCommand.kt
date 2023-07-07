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

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.popalay.barnee.data.model.DrinkMinimumData
import com.popalay.barnee.domain.addtocollection.AddToCollectionInput
import com.popalay.barnee.domain.navigation.AddToCollectionDestination
import com.popalay.barnee.domain.navigation.DrinkDestination.Companion.KEY_IDENTIFIER
import com.popalay.barnee.domain.navigation.DrinkDestination.Companion.KEY_IMAGE
import com.popalay.barnee.domain.navigation.DrinkDestination.Companion.KEY_NAME
import com.popalay.barnee.domain.navigation.RouteProvider
import com.popalay.barnee.util.toImageUrl

object AddToCollectionNavigationCommand : NavigationCommand<AddToCollectionInput>,
    RouteProvider by AddToCollectionDestination.Companion {
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_IDENTIFIER) { type = NavType.StringType },
        navArgument(KEY_IMAGE) { type = NavType.StringType },
        navArgument(KEY_NAME) { type = NavType.StringType }
    )

    override val deeplinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${DeeplinkPrefix}$route" }
    )

    override val navigationType: NavigationCommand.NavigationType = NavigationCommand.NavigationType.BottomSheet

    override fun parseInput(backStackEntry: NavBackStackEntry) = AddToCollectionInput(
        DrinkMinimumData(
            identifier = backStackEntry.arguments?.getString(KEY_IDENTIFIER).orEmpty(),
            displayImageUrl = backStackEntry.arguments?.getString(KEY_IMAGE).orEmpty().toImageUrl(),
            name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
        )
    )
}
