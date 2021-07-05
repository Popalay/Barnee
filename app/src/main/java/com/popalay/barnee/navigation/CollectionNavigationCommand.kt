package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.popalay.barnee.domain.collection.CollectionInput

object CollectionNavigationCommand : NavigationCommand {
    private const val KEY_NAME = "name"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_NAME) { type = NavType.StringType },
    )

    override val route: String = "collection/{$KEY_NAME}"

    fun destination(name: String): String = "collection/$name"

    fun parseInput(backStackEntry: NavBackStackEntry): CollectionInput {
        val name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
        return CollectionInput(name)
    }
}
