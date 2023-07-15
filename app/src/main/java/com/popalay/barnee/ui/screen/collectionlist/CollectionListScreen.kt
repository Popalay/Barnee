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

package com.popalay.barnee.ui.screen.collectionlist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.popalay.barnee.di.injectStateMachine
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.collectionlist.CollectionListState
import com.popalay.barnee.domain.collectionlist.CollectionListStateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.NavigateBackAction
import com.popalay.barnee.domain.navigation.NavigateToAction
import com.popalay.barnee.domain.navigation.ParcelableScreen
import com.popalay.barnee.domain.navigation.ReplaceCurrentScreenAction
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.util.asStateFlow
import io.matthewnelson.component.parcelize.Parcelize

@Parcelize
class CollectionListScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<CollectionListStateMachine>()
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        CollectionListScreen(state, stateMachine::dispatch)
    }
}

@Composable
private fun CollectionListScreen(state: CollectionListState, onAction: (Action) -> Unit) {
    LaunchedEffect(state.collections) {
        if (state.collections()?.size == 1) {
            onAction(ReplaceCurrentScreenAction(AppScreens.SingleCollection()))
        }
    }
    if (state.collections()?.size == 1) return

    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()

        ActionsAppBar(
            title = "Collections",
            modifier = Modifier.liftOnScroll(listState),
            leadingButtons = { BackButton(onClick = { onAction(NavigateBackAction) }) }
        )
        CollectionGrid(
            collections = state.collections,
            listState = listState,
            emptyMessage = "You don't have any collections yet\nstart adding them by clicking the ♥ button",
            onRetry = { },
            onItemClick = { onAction(NavigateToAction(AppScreens.SingleCollection(it))) },
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.navigationBars,
                additionalStart = 8.dp,
                additionalEnd = 8.dp
            )
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CollectionListScreenPreview() {
    BarneeTheme {
        CollectionListScreen(CollectionListState()) {}
    }
}
