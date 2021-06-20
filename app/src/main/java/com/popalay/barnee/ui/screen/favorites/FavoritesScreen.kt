package com.popalay.barnee.ui.screen.favorites

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.popalay.barnee.domain.favorites.FavoritesState
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.util.getViewModel

@Composable
fun FavoritesScreen() {
    FavoritesScreen(getViewModel())
}

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel) {
    val state by viewModel.stateFlow.collectAsState()
    FavoritesScreen(state)
}

@Composable
fun FavoritesScreen(state: FavoritesState) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        val lazyPagingItems = state.drinks.collectAsLazyPagingItems()

        ActionsAppBar(
            title = "Favorites",
            modifier = Modifier.liftOnScroll(listState),
            leadingButtons = { BackButton() }
        )
        DrinkGrid(
            drinks = lazyPagingItems,
            listState = listState,
            emptyMessage = "You don't have any favorite drinks yet\nstart adding them by clicking the ♥ button",
            onRetry = { lazyPagingItems.retry() },
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
fun FavoritesScreenPreview() {
    BarneeTheme {
        FavoritesScreen(FavoritesState())
    }
}