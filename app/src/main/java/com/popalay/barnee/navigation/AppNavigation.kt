package com.popalay.barnee.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.composable
import com.popalay.barnee.data.model.Drink

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No local navigator found!")
}

const val DEEPLINK_PREFIX = "https://barnee.com/"

object AppNavigation {
    fun root() = discovery()

    fun discovery() = DiscoveryNavigationCommand.destination

    fun drink(drink: Drink) = DrinkNavigationCommand.destination(drink.alias, drink.displayName, drink.displayImageUrl)

    fun favorites() = FavoriteDrinksNavigationCommand.destination

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