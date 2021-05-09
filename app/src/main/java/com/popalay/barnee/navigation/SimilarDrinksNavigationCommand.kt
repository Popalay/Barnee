package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument

data class SimilarDrinksScreenArgs(
    val alias: String,
    val name: String
) : ScreenArgs

object SimilarDrinksNavigationCommand : NavigationCommand {
    private const val KEY_LIKE = "like"
    private const val KEY_NAME = "name"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_LIKE) { type = NavType.StringType },
        navArgument(KEY_NAME) { type = NavType.StringType }
    )

    override val route: String = "drink?$KEY_LIKE={$KEY_LIKE}&$KEY_NAME={$KEY_NAME}"

    fun destination(
        alias: String,
        name: String
    ): String = "drink?$KEY_LIKE=$alias&$KEY_NAME=$name"

    fun parseArgs(backStackEntry: NavBackStackEntry) = SimilarDrinksScreenArgs(
        alias = backStackEntry.arguments?.getString(KEY_LIKE).orEmpty(),
        name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
    )
}