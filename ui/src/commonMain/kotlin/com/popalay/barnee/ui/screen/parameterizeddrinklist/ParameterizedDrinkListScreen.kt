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

package com.popalay.barnee.ui.screen.parameterizeddrinklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import com.moriatsushi.insetsx.navigationBars
import com.moriatsushi.insetsx.statusBarsPadding
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.navigation.NavigateBackAction
import com.popalay.barnee.domain.navigation.ScreenWithInputAsKey
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListState
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListStateMachine
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
data class ParameterizedDrinkListScreen(
    override val input: ParameterizedDrinkListInput
) : ScreenWithInputAsKey<ParameterizedDrinkListInput> {

    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<ParameterizedDrinkListStateMachine>(parameters = { parametersOf(input) })
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        ParameterizedDrinkListScreen(state, stateMachine::dispatch)
    }
}

@Composable
internal fun ParameterizedDrinkListScreen(
    state: ParameterizedDrinkListState,
    onAction: (Action) -> Unit,
    floatingActionButton: (@Composable () -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    var fabHeight by remember { mutableStateOf(0.dp) }

    val fab: @Composable () -> Unit = {
        Box(
            Modifier.onGloballyPositioned { coordinates ->
                fabHeight = with(density) { coordinates.size.height.toDp() }
            }
        ) { floatingActionButton?.invoke() }
    }

    Scaffold(
        topBar = {
            ActionsAppBar(
                title = buildAnnotatedString {
                    append(state.title)
                    withStyle(SpanStyle(color = MaterialTheme.colors.primary)) {
                        append(state.titleHighlighted)
                    }
                },
                modifier = Modifier
                    .liftOnScroll(listState)
                    .statusBarsPadding(),
                leadingButtons = { BackButton(onClick = { onAction(NavigateBackAction) })}
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = fab,
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            val lazyPagingItems = state.drinks.collectAsLazyPagingItems()

            DrinkGrid(
                drinks = lazyPagingItems,
                listState = listState,
                emptyMessage = state.emptyStateMessage,
                onRetry = { lazyPagingItems.retry() },
                contentPadding = WindowInsets.navigationBars.add(
                    WindowInsets(
                        left = 8.dp,
                        right = 8.dp,
                        bottom = fabHeight
                    )
                ).asPaddingValues()
            )
        }
    }
}
