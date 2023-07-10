/*
 * Copyright (c) 2023 Denys Nykyforov
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

import android.content.Intent
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import coil.Coil
import coil.ImageLoader
import coil.util.DebugLogger
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.popalay.barnee.data.message.Message
import com.popalay.barnee.data.message.MessagesProvider
import com.popalay.barnee.domain.navigation.Router
import com.popalay.barnee.domain.navigation.StackChange
import com.popalay.barnee.navigation.NavigationHost
import com.popalay.barnee.navigation.SlideTransition
import com.popalay.barnee.ui.common.PrimarySnackbar
import com.popalay.barnee.ui.screen.discovery.DiscoveryScreen
import com.popalay.barnee.ui.screen.shaketodrink.ShakeToDrinkScreen
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.util.ImageUrlCoilMapper
import com.popalay.barnee.ui.util.LifecycleAwareLaunchedEffect
import com.popalay.barnee.util.isDebug
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ComposeApp(intent: Intent) {
    ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
        val context = LocalContext.current
        val imageLoader = remember {
            ImageLoader.Builder(context)
                .logger(if (isDebug) DebugLogger() else null).components {
                    add(ImageUrlCoilMapper())
                }
                .build()
        }

        LaunchedEffect(Unit) {
            Firebase.dynamicLinks.getDynamicLink(intent).addOnSuccessListener { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let {
// TODO                    navController.popBackStack(navController.graph.id, false)
//                    navController.navigate(it)
                }
            }

            Coil.setImageLoader(imageLoader)
        }

        val snackbarHostState = remember { SnackbarHostState() }
        Box(modifier = Modifier.fillMaxSize()) {
            BottomSheetNavigator(
                sheetElevation = 1.dp,
                sheetBackgroundColor = MaterialTheme.colors.background,
                sheetContentColor = MaterialTheme.colors.onBackground,
                scrimColor = MaterialTheme.colors.background.copy(alpha = 0.7F),
            ) { bottomSheetNavigator ->
                Navigator(DiscoveryScreen()) { navigator ->
                    MessagesHost(snackbarHostState)
                    SlideTransition(navigator) { screen ->
                        NavigationHost(navigator, bottomSheetNavigator)
                        screen.Content()
                        ShakeToDrinkScreen().Content()
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { PrimarySnackbar(it) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun MessagesHost(snackbarHostState: SnackbarHostState, ) {
    val context = LocalContext.current
    val messagesFlow = koinInject<MessagesProvider>().messageFlow
    val snackbarScope = rememberCoroutineScope()
    val router = koinInject<Router>()

    LifecycleAwareLaunchedEffect(messagesFlow) { message ->
        when (message) {
            is Message.SnackBar -> {
                snackbarScope.coroutineContext.cancelChildren()
                snackbarScope.launch {
                    snackbarHostState.showSnackbar(message.content, actionLabel = message.action?.text).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            message.action?.destination?.let { router.updateStack(StackChange.Push(it)) }
                        }
                    }
                }
            }

            is Message.Toast    -> {
                val duration = when (message.duration) {
                    Message.Toast.Duration.Short -> Toast.LENGTH_SHORT
                    Message.Toast.Duration.Long  -> Toast.LENGTH_LONG
                }
                Toast.makeText(context, message.content, duration).show()
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ComposeAppPreview() {
    BarneeTheme {
        ComposeApp(Intent())
    }
}
