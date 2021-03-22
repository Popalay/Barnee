package com.popalay.barnee.ui.screen.navigation

import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No local navigator found!")
}

sealed class TabScreen(val route: String, val title: String, val icon: ImageVector) {
    object Search : TabScreen("search", "Search", Filled.Search)
    object Discovery : TabScreen("discovery", "Discovery", Filled.Home)
    object Favorites : TabScreen("favorites", "Favorites", Filled.FavoriteBorder)
}

val homeScreens = listOf(
    TabScreen.Search,
    TabScreen.Discovery,
    TabScreen.Favorites,
)

sealed class Screen(val route: String) {
    data class Drink(
        val alias: String,
        val name: String,
        val image: String
    ) : Screen("drink/$alias?image=$image&name=$name")

    data class Receipt(
        val steps: List<String>,
        val image: String,
        val video: String
    ) : Screen("receipt?steps=${steps.joinToString("::")}&image=$image&video=$video")

    data class CategoryDrinks(val tag: String) : Screen("drink?tag=$tag")
    data class SimilarDrinks(
        val alias: String,
        val name: String
    ) : Screen("drink?like=$alias&name=$name")
}