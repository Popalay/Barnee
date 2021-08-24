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

package com.popalay.barnee.ui.screen.app

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.plusAssign
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.util.DebugLogger
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.popalay.barnee.domain.app.AppAction
import com.popalay.barnee.domain.navigation.BackDestination
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.notification.Notification
import com.popalay.barnee.domain.notification.NotificationAction
import com.popalay.barnee.domain.notification.NotificationService
import com.popalay.barnee.domain.notification.NotificationShowPolicy
import com.popalay.barnee.navigation.AddToCollectionNavigationCommand
import com.popalay.barnee.navigation.CheckOutDrinkNavigationCommand
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
import com.popalay.barnee.navigation.navigationNodeBottomSheet
import com.popalay.barnee.navigation.navigationNodeDialog
import com.popalay.barnee.ui.common.PrimarySnackbar
import com.popalay.barnee.ui.screen.addtocollection.AddToCollectionScreen
import com.popalay.barnee.ui.screen.checkoutdrink.CheckOutDrinkScreen
import com.popalay.barnee.ui.screen.collection.CollectionScreen
import com.popalay.barnee.ui.screen.collectionlist.CollectionListScreen
import com.popalay.barnee.ui.screen.discovery.DiscoveryScreen
import com.popalay.barnee.ui.screen.drink.DrinkScreen
import com.popalay.barnee.ui.screen.parameterizeddrinklist.ParameterizedDrinkListScreen
import com.popalay.barnee.ui.screen.search.SearchScreen
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.util.ImageUrlCoilMapper
import com.popalay.barnee.ui.util.LifecycleAwareLaunchedEffect
import com.popalay.barnee.ui.util.findActivity
import com.popalay.barnee.ui.util.toSnackbarDuration
import com.popalay.barnee.util.isDebug
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun ComposeApp() {
    ComposeApp(getViewModel())
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ComposeApp(viewModel: AppViewModel) {
    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
        val context = LocalContext.current

        val imageLoader = remember {
            ImageLoader.Builder(context)
                .logger(if (isDebug) DebugLogger() else null)
                .componentRegistry {
                    add(ImageUrlCoilMapper())
                }
                .build()
        }

        CompositionLocalProvider(LocalImageLoader provides imageLoader) {
            NavigationGraph()
            Notifications { viewModel.processAction(AppAction.OnNotificationAction(it)) }
        }
    }
}

@Composable
private fun Notifications(onNotificationAction: (NotificationAction) -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val notificationService = get<NotificationService>()

    LifecycleAwareLaunchedEffect(notificationService.notificationFlow) { notification ->
        when (notification) {
            is Notification.Snackbar -> {
                when (notification.showPolicy) {
                    NotificationShowPolicy.Append -> Unit
                    NotificationShowPolicy.CancelPrevious -> snackbarScope.coroutineContext.cancelChildren()
                    NotificationShowPolicy.Drop -> if (snackbarScope.isActive) return@LifecycleAwareLaunchedEffect
                }
                snackbarScope.launch {
                    snackbarHostState.showSnackbar(
                        message = notification.message.toString(),
                        actionLabel = notification.action?.title,
                        duration = notification.duration.toSnackbarDuration()
                    ).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            notification.action?.let { onNotificationAction(it) }
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { PrimarySnackbar(it) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
private fun NavigationGraph() {
    val activity = requireNotNull(findActivity())
    val navController = rememberAnimatedNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    val router: Router = get()

    LaunchedEffect(activity) {
        Firebase.dynamicLinks.getDynamicLink(activity.intent)
            .addOnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->
                pendingDynamicLinkData?.link?.let {
                    navController.popBackStack(navController.graph.id, false)
                    navController.navigate(it)
                }
            }
    }

    LifecycleAwareLaunchedEffect(router.destinationFlow) { destination ->
        if (destination == BackDestination) navController.navigateUp()
        else navController.navigate(destination) {
            launchSingleTop = destination.launchSingleTop
        }
    }

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetElevation = 1.dp,
        sheetShape = MaterialTheme.shapes.large.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContentColor = MaterialTheme.colors.onBackground,
        scrimColor = MaterialTheme.colors.background.copy(alpha = 0.7F),
    ) {
        AnimatedNavHost(
            navController = navController,
            startDestination = DiscoveryNavigationCommand.route,
            enterTransition = { _, _ ->
                slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween()) +
                        fadeIn(animationSpec = tween())
            },
            exitTransition = { _, _ ->
                slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween()) +
                        fadeOut(animationSpec = tween())
            }
        ) {
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
            navigationNodeBottomSheet(AddToCollectionNavigationCommand) {
                AddToCollectionScreen(AddToCollectionNavigationCommand.parseInput(it))
            }
            navigationNodeDialog(CheckOutDrinkNavigationCommand) {
                CheckOutDrinkScreen()
            }
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
