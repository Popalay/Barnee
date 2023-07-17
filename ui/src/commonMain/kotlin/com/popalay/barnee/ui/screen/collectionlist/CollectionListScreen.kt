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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moriatsushi.insetsx.navigationBars
import com.moriatsushi.insetsx.statusBarsPadding
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
import com.popalay.barnee.ui.extensions.injectStateMachine
import com.popalay.barnee.ui.platform.collectAsStateWithLifecycle
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
            modifier = Modifier
                .liftOnScroll(listState)
                .statusBarsPadding(),
            leadingButtons = { BackButton(onClick = { onAction(NavigateBackAction) }) }
        )
        CollectionGrid(
            collections = state.collections,
            listState = listState,
            emptyMessage = "You don't have any collections yet\nstart adding them by clicking the ♥ button",
            onRetry = { },
            onItemClick = { onAction(NavigateToAction(AppScreens.SingleCollection(it))) },
            contentPadding = WindowInsets.navigationBars.add(
                WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
            ).asPaddingValues()
        )
    }
}
