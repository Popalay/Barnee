package com.popalay.barnee

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
import com.popalay.barnee.ui.screen.categorydrinks.CategoryDrinksScreen
import com.popalay.barnee.ui.screen.drink.DrinkScreen
import com.popalay.barnee.ui.screen.home.HomeScreen
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.screen.receipt.ReceiptScreen
import com.popalay.barnee.ui.screen.similardrinks.SimilarDrinksScreen
import com.popalay.barnee.ui.theme.BarneeTheme
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

@Composable
fun ComposeApp() {
    ProvideWindowInsets {
        val navController = rememberNavController()
        val insets = LocalWindowInsets.current

        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(navController, startDestination = "home") {
                composable("home") {
                    HomeScreen()
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
                    "receipt?steps={steps}&image={image}&video={video}",
                    arguments = listOf(
                        navArgument("steps") { type = NavType.StringType },
                        navArgument("image") { type = NavType.StringType },
                        navArgument("video") { type = NavType.StringType },
                    )
                ) {
                    ReceiptScreen(
                        steps = it.arguments?.getString("steps").orEmpty().split("::"),
                        image = it.arguments?.getString("image").orEmpty(),
                        video = it.arguments?.getString("video").orEmpty(),
                    )
                }
                composable(
                    "drink?tag={tag}",
                    arguments = listOf(navArgument("tag") { type = NavType.StringType })
                ) {
                    CategoryDrinksScreen(
                        tag = it.arguments?.getString("tag").orEmpty(),
                        contentPadding = insets.navigationBars.toPaddingValues()
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
                        contentPadding = insets.navigationBars.toPaddingValues()
                    )
                }
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    BarneeTheme {
        ComposeApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    BarneeTheme(darkTheme = true) {
        ComposeApp()
    }
}