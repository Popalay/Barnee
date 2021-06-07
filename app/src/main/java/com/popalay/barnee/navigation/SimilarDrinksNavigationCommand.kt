package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput

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

    fun parseInput(backStackEntry: NavBackStackEntry): ParameterizedDrinkListInput {
        val alias = backStackEntry.arguments?.getString(KEY_LIKE).orEmpty()
        val name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
        return ParameterizedDrinkListInput(
            request = DrinksRequest.RelatedTo(alias),
            title = "Cocktails like ",
            titleHighlighted = name
        )
    }
}