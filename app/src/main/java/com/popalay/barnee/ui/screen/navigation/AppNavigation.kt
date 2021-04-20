package com.popalay.barnee.ui.screen.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No local navigator found!")
}

sealed class Screen(val route: String) {
    data class Drink(
        val alias: String,
        val name: String,
        val image: String
    ) : Screen("drink/$alias?image=$image&name=$name")

    data class CategoryDrinks(val tag: String) : Screen("drink?tag=$tag")
    object Favorites : Screen("favorites")
    object Search : Screen("search")
    data class SimilarDrinks(
        val alias: String,
        val name: String
    ) : Screen("drink?like=$alias&name=$name")
    data class QueryDrinks(
        val query: String,
        val name: String
    ) : Screen("drink?query=$query&name=$name")
}