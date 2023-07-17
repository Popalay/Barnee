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

package com.popalay.barnee.ui.screen.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue.Hidden
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import com.moriatsushi.insetsx.ExperimentalSoftwareKeyboardApi
import com.moriatsushi.insetsx.imePadding
import com.moriatsushi.insetsx.navigationBarsPadding
import com.moriatsushi.insetsx.statusBarsPadding
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.domain.Action
import com.popalay.barnee.domain.navigation.NavigateBackAction
import com.popalay.barnee.domain.navigation.ParcelableScreen
import com.popalay.barnee.domain.search.SearchAction
import com.popalay.barnee.domain.search.SearchState
import com.popalay.barnee.domain.search.SearchStateMachine
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.BarneeTextField
import com.popalay.barnee.ui.common.EmptyStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.drawBadge
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.extensions.injectStateMachine
import com.popalay.barnee.ui.icons.Done
import com.popalay.barnee.ui.icons.Filter
import com.popalay.barnee.ui.icons.Search
import com.popalay.barnee.ui.platform.collectAsStateWithLifecycle
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.util.asStateFlow
import com.popalay.barnee.util.displayNames
import io.matthewnelson.component.parcelize.Parcelize

@Parcelize
class SearchScreen : ParcelableScreen {
    @Composable
    override fun Content() {
        val stateMachine = injectStateMachine<SearchStateMachine>()
        val state by stateMachine.stateFlow.asStateFlow().collectAsStateWithLifecycle()

        SearchScreen(state, stateMachine::dispatch)
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class, ExperimentalSoftwareKeyboardApi::class)
@Composable
private fun SearchScreen(
    state: SearchState,
    onAction: (Action) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = Hidden)
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.showFilters) { ->
        if (state.showFilters) {
            keyboardController?.hide()
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible) {
            onAction(SearchAction.FiltersDismissed)
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetElevation = 1.dp,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContentColor = MaterialTheme.colors.onBackground,
        scrimColor = MaterialTheme.colors.background.copy(alpha = 0.7F),
        sheetContent = {
            StateLayout(
                value = state.aggregation,
                loadingState = { LoadingStateView() },
                emptyState = { EmptyStateView("Please try again") },
                errorState = { EmptyStateView("Please try again") }
            ) { aggregation ->
                Filters(
                    aggregation = aggregation,
                    selected = state.selectedFilters,
                    onApplyClick = { onAction(SearchAction.FiltersApplied) },
                    onFilterClicked = { onAction(SearchAction.FilterClicked(it)) }
                )
            }
        }
    ) {
        Scaffold { contentPadding ->
            Column(Modifier.padding(contentPadding)) {
                val listState = rememberLazyListState()
                val lazyPagingItems = state.drinks.collectAsLazyPagingItems()

                SearchAppBar(
                    onFilterClick = { onAction(SearchAction.ShowFiltersClicked) },
                    onBackClick = { onAction(NavigateBackAction) },
                    isFiltersApplied = state.selectedFilters.isNotEmpty(),
                    modifier = Modifier.liftOnScroll(listState)
                )
                DrinkGrid(
                    drinks = lazyPagingItems,
                    emptyMessage = "We currently have no drinks\non your request",
                    onRetry = {
                        lazyPagingItems.retry()
                        onAction(SearchAction.Retry)
                    },
                    listState = listState,
                    modifier = Modifier.weight(1F),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                )
                SearchTextField(
                    value = state.searchQuery,
                    onValueChange = { onAction(SearchAction.QueryChanged(it)) },
                    onClearClicked = { onAction(SearchAction.ClearSearchQuery) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp)
                        .imePadding()
                )
            }
        }
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onClearClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    BarneeTextField(
        value = value,
        leadingIcon = {
            Icon(
                imageVector = Icons.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = value.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onClearClicked) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        placeholder = {
            Text(
                text = "Margarita",
                style = MaterialTheme.typography.body1
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface,
            textColor = MaterialTheme.colors.primary,
            leadingIconColor = MaterialTheme.colors.primary
        ),
        singleLine = true,
        onValueChange = onValueChange,
        modifier = modifier
            .padding(24.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.primary,
                shape = CircleShape
            )
    )
}

@Composable
private fun SearchAppBar(
    onFilterClick: () -> Unit,
    onBackClick: () -> Unit,
    isFiltersApplied: Boolean,
    modifier: Modifier = Modifier,
) {
    val badgeRadius by animateDpAsState(if (isFiltersApplied) 3.dp else 0.dp)
    ActionsAppBar(
        title = "Search",
        modifier = modifier.statusBarsPadding(),
        leadingButtons = { BackButton(onClick = onBackClick) },
        trailingButtons = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Filter,
                    contentDescription = "Filter",
                    modifier = Modifier.drawBadge(MaterialTheme.colors.onBackground, badgeRadius)
                )
            }
        }
    )
}

@Composable
private fun Filters(
    aggregation: Aggregation,
    selected: Set<Pair<String, AggregationGroup>>,
    onApplyClick: () -> Unit,
    onFilterClicked: (Pair<String, AggregationGroup>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(start = 16.dp)
        ) {
            Text(
                text = "Apply all your wishes",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
            IconButton(onClick = onApplyClick) {
                Icon(
                    imageVector = Icons.Done,
                    tint = MaterialTheme.colors.primary,
                    contentDescription = "Your bartender",
                )
            }

        }
        val groups = listOf(
            "Color" to aggregation.colored,
            "Taste" to aggregation.tasting,
            "Made with" to aggregation.withType,
            "Served in" to aggregation.servedIn,
            "Skill lvl" to aggregation.skill
        )
        groups.forEachIndexed { index, (title, group) ->
            AggregationGroup(
                name = title,
                group = group,
                selected = selected,
                onClick = onFilterClicked,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            if (index != groups.lastIndex) {
                Divider(modifier = Modifier.padding(vertical = 16.dp))
            } else {
                Spacer(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AggregationGroup(
    name: String,
    group: AggregationGroup,
    selected: Set<Pair<String, AggregationGroup>>,
    onClick: (Pair<String, AggregationGroup>) -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = name,
        style = MaterialTheme.typography.subtitle1,
        modifier = modifier
    )
    Spacer(modifier = Modifier.height(16.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
//        crossAxisSpacing = 16.dp,
        modifier = modifier
    ) {
        group.displayNames.forEach { (alias, displayName) ->
            val isSelected = alias to group in selected
            Text(
                text = displayName,
                color = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .defaultMinSize(minWidth = 92.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colors.primary else Color.Transparent, CircleShape)
                    .border(1.dp, MaterialTheme.colors.primary, CircleShape)
                    .clickable { onClick(alias to group) }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}
