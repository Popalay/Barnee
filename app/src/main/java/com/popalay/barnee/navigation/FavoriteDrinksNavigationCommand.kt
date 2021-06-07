package com.popalay.barnee.navigation

import androidx.navigation.NavBackStackEntry
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput

object FavoriteDrinksNavigationCommand : NavigationCommand {
    override val route: String = "favorites"
    const val destination = "favorites"

    @Suppress("UNUSED_PARAMETER")
    fun parseInput(backStackEntry: NavBackStackEntry): ParameterizedDrinkListInput =
        ParameterizedDrinkListInput(
            request = DrinksRequest.Favorites,
            title = "Favorites"
        )
}