package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import androidx.navigation.navDeepLink

data class DrinkScreenArgs(
    val alias: String,
    val name: String,
    val image: String
) : ScreenArgs

object DrinkNavigationCommand : NavigationCommand {
    private const val KEY_ALIAS = "alias"
    private const val KEY_NAME = "name"
    private const val KEY_IMAGE = "image"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_ALIAS) { type = NavType.StringType },
        navArgument(KEY_IMAGE) { type = NavType.StringType },
        navArgument(KEY_NAME) { type = NavType.StringType }
    )

    override val deeplinks: List<NavDeepLink> = listOf(
        navDeepLink { uriPattern = "${DEEPLINK_PREFIX}drink/{$KEY_ALIAS}" }
    )

    override val route: String = "drink/{$KEY_ALIAS}?$KEY_IMAGE={$KEY_IMAGE}&$KEY_NAME={$KEY_NAME}"

    fun destination(
        alias: String,
        name: String,
        image: String
    ): String = "drink/$alias?$KEY_IMAGE=$image&$KEY_NAME=$name"

    fun parseArgs(backStackEntry: NavBackStackEntry) = DrinkScreenArgs(
        alias = backStackEntry.arguments?.getString(KEY_ALIAS).orEmpty(),
        image = backStackEntry.arguments?.getString(KEY_IMAGE).orEmpty(),
        name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
    )
}