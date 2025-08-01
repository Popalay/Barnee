/*
 * Copyright (c) 2025 Denys Nykyforov
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

package com.popalay.barnee.ui.screen.collection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import com.moriatsushi.insetsx.navigationBars
import com.moriatsushi.insetsx.statusBarsPadding
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.collection.CollectionAction
import com.popalay.barnee.domain.collection.CollectionInput
import com.popalay.barnee.domain.collection.CollectionState
import com.popalay.barnee.domain.collection.CollectionStateMachine
import com.popalay.barnee.domain.navigation.NavigateBackAction
import com.popalay.barnee.domain.navigation.ScreenWithInputAsKey
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.extensions.injectStateMachine
import com.popalay.barnee.ui.platform.collectAsStateWithLifecycle
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.util.asStateFlow
import io.matthewnelson.component.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

@Parcelize
data class CollectionScreen(override val input: CollectionInput) : ScreenWithInputAsKey<CollectionInput> {

    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<CollectionStateMachine>(parameters = { parametersOf(input) })
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        CollectionScreen(state, stateMachine::dispatch)
    }
}

@Composable
private fun CollectionScreen(
    state: CollectionState,
    onAction: (Action) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        val lazyPagingItems = state.drinks.collectAsLazyPagingItems()

        ActionsAppBar(
            title = state.name,
            modifier = Modifier
                .liftOnScroll(listState)
                .statusBarsPadding(),
            leadingButtons = { BackButton(onClick = { onAction(NavigateBackAction) }) },
            trailingButtons = {
                AnimatedVisibility(state.isShareButtonVisible) {
                    IconButton(onClick = { onAction(CollectionAction.ShareClicked) }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share drink",
                        )
                    }
                }
                AnimatedVisibility(state.isRemoveButtonVisible) {
                    IconButton(onClick = { onAction(CollectionAction.RemoveClicked) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove collection",
                        )
                    }
                }
                AnimatedVisibility(state.isSaveButtonVisible) {
                    IconButton(onClick = { onAction(CollectionAction.SaveClicked) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Save collection",
                        )
                    }
                }
            }
        )
        DrinkGrid(
            drinks = lazyPagingItems,
            listState = listState,
            emptyMessage = "You don't have any favorite drinks yet\nstart adding them by clicking the ♥ button",
            onRetry = { lazyPagingItems.retry() },
            contentPadding = WindowInsets.navigationBars.add(
                WindowInsets(
                    left = 8.dp,
                    right = 8.dp,
                )
            ).asPaddingValues()
        )
    }
}
