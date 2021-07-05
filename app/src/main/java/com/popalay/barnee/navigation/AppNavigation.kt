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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.displayName

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No local navigator found!")
}

const val DEEPLINK_PREFIX = "https://barnee.com/"

object AppNavigation {
    fun root() = discovery()

    fun discovery() = DiscoveryNavigationCommand.destination

    fun drink(drink: Drink) = DrinkNavigationCommand.destination(drink.alias, drink.displayName, drink.displayImageUrl)

    fun collections() = CollectionsNavigationCommand.destination

    fun search() = SearchNavigationCommand.destination

    fun queryDrinks(
        query: String,
        name: String
    ) = QueryDrinksNavigationCommand.destination(query, name)

    fun similarDrinks(
        alias: String,
        name: String
    ) = SimilarDrinksNavigationCommand.destination(alias, name)

    fun tagDrinks(tag: String) = TagDrinksNavigationCommand.destination(tag)

    fun collection(collection: Collection) = CollectionNavigationCommand.destination(collection.name)
}

fun NavGraphBuilder.navigationNode(
    command: NavigationCommand,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = command.route,
        deepLinks = command.deeplinks,
        arguments = command.arguments,
        content = content
    )
}

interface NavigationCommand {
    val arguments: List<NamedNavArgument> get() = emptyList()
    val deeplinks: List<NavDeepLink> get() = emptyList()
    val route: String
}
