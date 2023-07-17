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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import com.moriatsushi.insetsx.navigationBars
import com.moriatsushi.insetsx.statusBarsPadding
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
import com.popalay.barnee.ui.platform.collectAsStateWithLifecycle
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
            contentPadding = WindowInsets.navigationBars.add(
                WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
            ).asPaddingValues()
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
        title = "Barnee",
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
