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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.util.DebugLogger
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.popalay.barnee.domain.navigation.BackDestination
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.navigation.CollectionNavigationCommand
import com.popalay.barnee.navigation.CollectionsNavigationCommand
import com.popalay.barnee.navigation.DiscoveryNavigationCommand
import com.popalay.barnee.navigation.DrinkNavigationCommand
import com.popalay.barnee.navigation.QueryDrinksNavigationCommand
import com.popalay.barnee.navigation.SearchNavigationCommand
import com.popalay.barnee.navigation.SimilarDrinksNavigationCommand
import com.popalay.barnee.navigation.TagDrinksNavigationCommand
import com.popalay.barnee.navigation.navigate
import com.popalay.barnee.navigation.navigationNode
import com.popalay.barnee.ui.screen.addtocollection.AddToCollectionScreen
import com.popalay.barnee.ui.screen.collection.CollectionScreen
import com.popalay.barnee.ui.screen.collectionlist.CollectionListScreen
import com.popalay.barnee.ui.screen.discovery.DiscoveryScreen
import com.popalay.barnee.ui.screen.drink.DrinkScreen
import com.popalay.barnee.ui.screen.parameterizeddrinklist.ParameterizedDrinkListScreen
import com.popalay.barnee.ui.screen.search.SearchScreen
import com.popalay.barnee.ui.screen.shaketodrink.ShakeToDrinkScreen
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.util.ImageUrlCoilMapper
import com.popalay.barnee.ui.util.LifecycleAwareLaunchedEffect
import com.popalay.barnee.util.isDebug
import org.koin.androidx.compose.get

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

    @OptIn(ExperimentalAnimatedInsets::class)
    @Composable
    fun ComposeApp() {
        ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
            val context = LocalContext.current
            val navController = rememberNavController()
            val imageLoader = remember {
                ImageLoader.Builder(context)
                    .logger(if (isDebug) DebugLogger() else null)
                    .componentRegistry {
                        add(ImageUrlCoilMapper())
                    }
                    .build()
            }

            LaunchedEffect(Unit) {
                Firebase.dynamicLinks.getDynamicLink(intent)
                    .addOnSuccessListener { pendingDynamicLinkData ->
                        pendingDynamicLinkData?.link?.let {
                            navController.popBackStack(navController.graph.id, false)
                            navController.navigate(it)
                        }
                    }
            }

            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                NavigationGraph(navController)
                AddToCollectionScreen()
                ShakeToDrinkScreen()
            }
        }
    }

    @Composable
    fun NavigationGraph(navController: NavHostController) {
        val router: Router = get()

        LifecycleAwareLaunchedEffect(router.destinationFlow) { destination ->
            if (destination == BackDestination) navController.navigateUp()
            else navController.navigate(destination)
        }

        NavHost(navController, startDestination = DiscoveryNavigationCommand.route) {
            navigationNode(DiscoveryNavigationCommand) {
                DiscoveryScreen()
            }
            navigationNode(DrinkNavigationCommand) {
                DrinkScreen(DrinkNavigationCommand.parseInput(it))
            }
            navigationNode(TagDrinksNavigationCommand) {
                ParameterizedDrinkListScreen(TagDrinksNavigationCommand.parseInput(it))
            }
            navigationNode(SimilarDrinksNavigationCommand) {
                ParameterizedDrinkListScreen(SimilarDrinksNavigationCommand.parseInput(it))
            }
            navigationNode(QueryDrinksNavigationCommand) {
                ParameterizedDrinkListScreen(QueryDrinksNavigationCommand.parseInput(it))
            }
            navigationNode(CollectionsNavigationCommand) {
                CollectionListScreen()
            }
            navigationNode(CollectionNavigationCommand) {
                CollectionScreen(CollectionNavigationCommand.parseInput(it))
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
