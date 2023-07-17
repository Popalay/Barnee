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

package com.popalay.barnee.ui.screen.discovery

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.popalay.barnee.R.string
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.discovery.DiscoveryState
import com.popalay.barnee.domain.discovery.DiscoveryStateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.NavigateToAction
import com.popalay.barnee.domain.navigation.ParcelableScreen
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.discovery.CategoryGrid
import com.popalay.barnee.ui.extensions.injectStateMachine
import com.popalay.barnee.ui.icons.Bartender
import com.popalay.barnee.ui.icons.HeartFilled
import com.popalay.barnee.ui.icons.Search
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.util.asStateFlow
import io.matthewnelson.component.parcelize.Parcelize

@Parcelize
class DiscoveryScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<DiscoveryStateMachine>()
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        DiscoveryScreen(state, stateMachine::dispatch)
    }
}

@Composable
private fun DiscoveryScreen(state: DiscoveryState, onAction: (Action) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        DiscoveryAppBar(
            onHeartClick = { onAction(NavigateToAction(AppScreens.Collections)) },
            onSearchClick = { onAction(NavigateToAction(AppScreens.Search)) },
            onBartenderClick = { onAction(NavigateToAction(AppScreens.GeneratedDrinks)) },
            modifier = Modifier.liftOnScroll(listState)
        )
        CategoryGrid(
            categories = state.categories,
            emptyMessage = "We currently have no drinks",
            onItemClick = { onAction(NavigateToAction(AppScreens.SingleCategory(it))) },
            listState = listState,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.navigationBars,
                additionalStart = 8.dp,
                additionalEnd = 8.dp
            )
        )
    }
}

@Composable
private fun DiscoveryAppBar(
    onHeartClick: () -> Unit,
    onSearchClick: () -> Unit,
    onBartenderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ActionsAppBar(
        title = stringResource(string.app_name),
        modifier = modifier.statusBarsPadding(),
        trailingButtons = {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {
                IconButton(onClick = onBartenderClick) {
                    Icon(
                        imageVector = Icons.Bartender,
                        contentDescription = "Your bartender",
                    )
                }
                IconButton(onClick = onHeartClick) {
                    Icon(
                        imageVector = Icons.HeartFilled,
                        contentDescription = "Favorites",
                    )
                }
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Search,
                        contentDescription = "Search",
                    )
                }
            }
        }
    )
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DiscoveryScreenPreview() {
    BarneeTheme {
        DiscoveryScreen()
    }
}
