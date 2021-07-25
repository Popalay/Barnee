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

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.navArgument
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.state.navigation.RouteProvider
import com.popalay.barnee.domain.state.navigation.SimilarDrinksDestination
import com.popalay.barnee.domain.state.navigation.SimilarDrinksDestination.Companion.KEY_LIKE
import com.popalay.barnee.domain.state.navigation.SimilarDrinksDestination.Companion.KEY_NAME
import com.popalay.barnee.domain.state.parameterizeddrinklist.ParameterizedDrinkListInput

object SimilarDrinksNavigationCommand : NavigationCommand<ParameterizedDrinkListInput>,
    RouteProvider by SimilarDrinksDestination.Companion {
    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(KEY_LIKE) { type = NavType.StringType },
        navArgument(KEY_NAME) { type = NavType.StringType }
    )

    override fun parseInput(backStackEntry: NavBackStackEntry): ParameterizedDrinkListInput {
        val alias = backStackEntry.arguments?.getString(KEY_LIKE).orEmpty()
        val name = backStackEntry.arguments?.getString(KEY_NAME).orEmpty()
        return ParameterizedDrinkListInput(
            request = DrinksRequest.RelatedTo(alias),
            title = "Cocktails like ",
            titleHighlighted = name
        )
    }
}
