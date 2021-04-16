package com.popalay.barnee

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.popalay.barnee.ui.screen.categorydrinks.CategoryDrinksScreen
import com.popalay.barnee.ui.screen.discovery.DiscoveryScreen
import com.popalay.barnee.ui.screen.drink.DrinkScreen
import com.popalay.barnee.ui.screen.favorites.FavoritesScreen
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.screen.navigation.Screen
import com.popalay.barnee.ui.screen.search.SearchScreen
import com.popalay.barnee.ui.screen.similardrinks.SimilarDrinksScreen
import com.popalay.barnee.ui.theme.BarneeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Barnee)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BarneeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ComposeApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimatedInsets::class)
@Composable
fun ComposeApp() {
    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
        val navController = rememberNavController()

        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(navController, startDestination = "home") {
                composable("home") {
                    DiscoveryScreen()
                }
                composable(
                    "drink/{alias}?image={image}&name={name}",
                    arguments = listOf(
                        navArgument("alias") { type = NavType.StringType },
                        navArgument("image") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType }
                    )
                ) {
                    DrinkScreen(
                        alias = it.arguments?.getString("alias").orEmpty(),
                        image = it.arguments?.getString("image").orEmpty(),
                        name = it.arguments?.getString("name").orEmpty()
                    )
                }
                composable(
                    "drink?tag={tag}",
                    arguments = listOf(navArgument("tag") { type = NavType.StringType })
                ) {
                    CategoryDrinksScreen(
                        tag = it.arguments?.getString("tag").orEmpty(),
                    )
                }
                composable(
                    "drink?like={alias}&name={name}",
                    arguments = listOf(
                        navArgument("alias") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType }
                    )
                ) {
                    SimilarDrinksScreen(
                        alias = it.arguments?.getString("alias").orEmpty(),
                        name = it.arguments?.getString("name").orEmpty(),
                    )
                }
                composable(Screen.Search.route) {
                    SearchScreen()
                }
                composable(Screen.Favorites.route) {
                    FavoritesScreen()
                }
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LightPreview() {
    BarneeTheme {
        ComposeApp()
    }
}