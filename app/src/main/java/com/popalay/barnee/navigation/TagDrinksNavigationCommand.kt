package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument

data class TagDrinksScreenArgs(val tag: String) : ScreenArgs

object TagDrinksNavigationCommand : NavigationCommand {
    private const val KEY_TAG = "tag"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_TAG) { type = NavType.StringType },
    )

    override val route: String = "drink?$KEY_TAG={$KEY_TAG}"

    fun destination(tag: String): String = "drink?$KEY_TAG=$tag"

    fun parseArgs(backStackEntry: NavBackStackEntry) = TagDrinksScreenArgs(
        tag = backStackEntry.arguments?.getString(KEY_TAG).orEmpty(),
    )
}