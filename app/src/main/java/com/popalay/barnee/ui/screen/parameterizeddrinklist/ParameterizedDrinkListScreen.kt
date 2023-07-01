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

package com.popalay.barnee.ui.screen.parameterizeddrinklist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.popalay.barnee.data.repository.DrinksRequest
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListInput
import com.popalay.barnee.domain.parameterizeddrinklist.ParameterizedDrinkListState
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.ui.theme.BarneeTheme
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ParameterizedDrinkListScreen(
    input: ParameterizedDrinkListInput,
    floatingActionButton: (@Composable () -> Unit)? = null
) {
    ParameterizedDrinkListScreen(getViewModel { parametersOf(input) }, floatingActionButton)
}

@Composable
fun ParameterizedDrinkListScreen(
    viewModel: ParameterizedDrinkListViewModel,
    floatingActionButton: (@Composable () -> Unit)?
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    ParameterizedDrinkListScreen(state, floatingActionButton)
}

@Composable
fun ParameterizedDrinkListScreen(
    state: ParameterizedDrinkListState,
    floatingActionButton: (@Composable () -> Unit)?
) {
    val listState = rememberLazyListState()
    val localDensity = LocalDensity.current
    var fabHeight by remember { mutableStateOf(0.dp) }

    val fab: @Composable () -> Unit = {
        Box(
            Modifier.onGloballyPositioned { coordinates ->
                fabHeight = with(localDensity) { coordinates.size.height.toDp() }
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
                modifier = Modifier.liftOnScroll(listState),
                leadingButtons = { BackButton() }
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
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.navigationBars,
                    additionalStart = 8.dp,
                    additionalEnd = 8.dp,
                    additionalBottom = fabHeight
                ),
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ParameterizedDrinkListScreenPreview() {
    BarneeTheme {
        ParameterizedDrinkListScreen(ParameterizedDrinkListState(DrinksRequest.ForQuery("query"), "Title", "Highlighted", "Empty"), {})
    }
}
