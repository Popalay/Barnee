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

package com.popalay.barnee.ui.screen.discovery

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.popalay.barnee.R
import com.popalay.barnee.R.string
import com.popalay.barnee.domain.discovery.DiscoveryAction
import com.popalay.barnee.domain.discovery.DiscoveryState
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.util.collectAsStateWithLifecycle
import org.koin.androidx.compose.getViewModel

@Composable
fun DiscoveryScreen() {
    DiscoveryScreen(getViewModel())
}

@Composable
private fun DiscoveryScreen(viewModel: DiscoveryViewModel) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    DiscoveryScreen(state, viewModel::processAction)
}

@Composable
private fun DiscoveryScreen(state: DiscoveryState, onAction: (DiscoveryAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        DiscoveryAppBar(
            onHeartClick = { onAction(DiscoveryAction.HeartClicked) },
            onSearchClick = { onAction(DiscoveryAction.SearchClicked) },
            modifier = Modifier.liftOnScroll(listState)
        )
        CategoryGrid(
            categories = state.categories,
            emptyMessage = "We currently have no drinks",
            onItemClick = { onAction(DiscoveryAction.CategoryClicked(it)) },
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
    modifier: Modifier = Modifier
) {
    ActionsAppBar(
        title = stringResource(string.app_name),
        modifier = modifier,
        trailingButtons = {
            IconButton(onClick = onHeartClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorites),
                    contentDescription = "Favorites",
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search",
                )
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
