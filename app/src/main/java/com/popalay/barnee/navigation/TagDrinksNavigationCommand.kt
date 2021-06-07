package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.ui.util.capitalizeFirstChar

object TagDrinksNavigationCommand : NavigationCommand {
    private const val KEY_TAG = "tag"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_TAG) { type = NavType.StringType },
    )

    override val route: String = "drink?$KEY_TAG={$KEY_TAG}"

    fun destination(tag: String): String = "drink?$KEY_TAG=$tag"

    fun parseInput(backStackEntry: NavBackStackEntry): ParameterizedDrinkListInput {
        val tag = backStackEntry.arguments?.getString(KEY_TAG).orEmpty()
        return ParameterizedDrinkListInput(
            request = DrinksRequest.ForTags(setOf(tag)),
            title = tag.capitalizeFirstChar()
        )
    }
}