package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.ui.util.capitalizeFirstChar

object QueryDrinksNavigationCommand : NavigationCommand {
    private const val KEY_QUERY = "query"
    private const val KEY_NAME = "name"

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_QUERY) { type = NavType.StringType },
        navArgument(KEY_NAME) { type = NavType.StringType }
    )

    override val route: String = "drink?$KEY_QUERY={$KEY_QUERY}&$KEY_NAME={$KEY_NAME}"

    fun destination(
        query: String,
        name: String
    ): String = "drink?$KEY_QUERY=$query&$KEY_NAME=$name"

    fun parseInput(backStackEntry: NavBackStackEntry): ParameterizedDrinkListInput {
        val query = backStackEntry.arguments?.getString(KEY_QUERY).orEmpty()
        val name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
        return ParameterizedDrinkListInput(
            request = DrinksRequest.ForQuery(query),
            title = name.capitalizeFirstChar()
        )
    }
}
