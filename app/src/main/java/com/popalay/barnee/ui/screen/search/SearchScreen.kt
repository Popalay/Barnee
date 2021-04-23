package com.popalay.barnee.ui.screen.search

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.popalay.barnee.R
import com.popalay.barnee.data.model.Aggregation
import com.popalay.barnee.data.model.AggregationGroup
import com.popalay.barnee.domain.search.SearchAction
import com.popalay.barnee.domain.search.SearchAction.QueryChanged
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.EmptyStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.drawBadge
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.ui.theme.BarneeTheme
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen() {
    val viewModel: SearchViewModel = getViewModel()
    val state by viewModel.stateFlow.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = Hidden)
    val textInputService = LocalTextInputService.current

    LaunchedEffect(state.isFiltersShown) {
        if (!bottomSheetState.isVisible && state.isFiltersShown) {
            textInputService?.hideSoftwareKeyboard()
            bottomSheetState.show()
        } else if (bottomSheetState.isVisible && !state.isFiltersShown) {
            bottomSheetState.hide()
        }
    }

    LaunchedEffect(!bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible) {
            viewModel.processAction(SearchAction.FiltersDismissed)
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
                    onFilterClicked = { viewModel.processAction(SearchAction.FilterClicked(it)) }
                )
            }
        }
    ) {
        Scaffold {
            Column {
                val listState = rememberLazyListState()
                SearchAppBar(
                    onFilterClick = { viewModel.processAction(SearchAction.ShowFiltersClicked) },
                    isFiltersApplied = state.selectedFilters.isNotEmpty(),
                    modifier = Modifier.liftOnScroll(listState)
                )
                DrinkGrid(
                    drinks = state.drinks,
                    emptyMessage = "We currently have no drinks\non your request",
                    listState = listState,
                    modifier = Modifier.weight(1F)
                )
                SearchTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.processAction(QueryChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp)
                        .navigationBarsWithImePadding()
                )
            }
        }
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search"
            )
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
    isFiltersApplied: Boolean,
    modifier: Modifier = Modifier,
) {
    val badgeRadius by animateDpAsState(if (isFiltersApplied) 3.dp else 0.dp)
    ActionsAppBar(
        title = "Search",
        modifier = modifier,
        leadingButtons = { BackButton() },
        trailingButtons = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_filter),
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
    onFilterClicked: (Pair<String, AggregationGroup>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(16.dp))
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
                Divider(modifier = Modifier.padding(vertical = 24.dp))
            } else {
                Spacer(modifier = Modifier.navigationBarsHeight(additional = 24.dp))
            }
        }
    }
}

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
    Spacer(modifier = Modifier.height(24.dp))
    FlowRow(
        mainAxisSpacing = 24.dp,
        crossAxisSpacing = 16.dp,
        modifier = modifier
    ) {
        group.displayNames.forEach { (alias, displayName) ->
            val isSelected = alias to group in selected
            Text(
                text = displayName,
                color = if (isSelected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body2,
                textAlign = Center,
                modifier = Modifier
                    .defaultMinSize(minWidth = 92.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colors.primary else Color.Transparent, CircleShape)
                    .border(Dp.Hairline, MaterialTheme.colors.primary, CircleShape)
                    .clickable { onClick(alias to group) }
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            )
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchScreenPreview() {
    BarneeTheme {
        SearchScreen()
    }
}