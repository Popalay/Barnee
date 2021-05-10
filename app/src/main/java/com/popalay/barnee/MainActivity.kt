package com.popalay.barnee

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.util.DebugLogger
import com.google.accompanist.coil.LocalImageLoader
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.popalay.barnee.navigation.AppNavigation
import com.popalay.barnee.navigation.DiscoveryNavigationCommand
import com.popalay.barnee.navigation.DrinkNavigationCommand
import com.popalay.barnee.navigation.CollectionsNavigationCommand
import com.popalay.barnee.navigation.LocalNavController
import com.popalay.barnee.navigation.QueryDrinksNavigationCommand
import com.popalay.barnee.navigation.SearchNavigationCommand
import com.popalay.barnee.navigation.SimilarDrinksNavigationCommand
import com.popalay.barnee.navigation.TagDrinksNavigationCommand
import com.popalay.barnee.navigation.navigationNode
import com.popalay.barnee.ui.screen.addtocollection.AddToCollectionScreen
import com.popalay.barnee.ui.screen.discovery.DiscoveryScreen
import com.popalay.barnee.ui.screen.drink.DrinkScreen
import com.popalay.barnee.ui.screen.parameterizeddrinklist.CollectionsScreen
import com.popalay.barnee.ui.screen.parameterizeddrinklist.QueryDrinksScreen
import com.popalay.barnee.ui.screen.parameterizeddrinklist.SimilarDrinksScreen
import com.popalay.barnee.ui.screen.parameterizeddrinklist.TagDrinksScreen
import com.popalay.barnee.ui.screen.search.SearchScreen
import com.popalay.barnee.ui.screen.shaketodrink.ShakeToDrinkScreen
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

    @OptIn(ExperimentalAnimatedInsets::class)
    @Composable
    fun ComposeApp() {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                Firebase.dynamicLinks.getDynamicLink(intent)
                    .addOnSuccessListener { pendingDynamicLinkData ->
                        pendingDynamicLinkData?.link?.let {
                            navController.popBackStack(navController.graph.id, false)
                            navController.navigate(it)
                        }
                    }
            }

            CompositionLocalProvider(
                LocalNavController provides navController,
                LocalImageLoader provides ImageLoader.Builder(LocalContext.current).logger(DebugLogger()).build()
            ) {
                NavigationGraph()
                AddToCollectionScreen()
                ShakeToDrinkScreen()
            }
        }
    }

    @Composable
    private fun NavigationGraph() {
        NavHost(LocalNavController.current, startDestination = AppNavigation.root()) {
            navigationNode(DiscoveryNavigationCommand) {
                DiscoveryScreen()
            }
            navigationNode(DrinkNavigationCommand) {
                DrinkScreen(DrinkNavigationCommand.parseArgs(it))
            }
            navigationNode(TagDrinksNavigationCommand) {
                TagDrinksScreen(TagDrinksNavigationCommand.parseArgs(it))
            }
            navigationNode(SimilarDrinksNavigationCommand) {
                SimilarDrinksScreen(SimilarDrinksNavigationCommand.parseArgs(it))
            }
            navigationNode(QueryDrinksNavigationCommand) {
                QueryDrinksScreen(QueryDrinksNavigationCommand.parseArgs(it))
            }
            navigationNode(CollectionsNavigationCommand) {
                CollectionsScreen()
            }
            navigationNode(SearchNavigationCommand) {
                SearchScreen()
            }
        }
    }

    @Preview("Light Theme", widthDp = 360, heightDp = 640)
    @Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun ComposeAppPreview() {
        BarneeTheme {
            ComposeApp()
        }
    }
}