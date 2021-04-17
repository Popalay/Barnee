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
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.screen.drinklist.DrinkGrid
import com.popalay.barnee.ui.theme.BarneeTheme
import org.koin.androidx.compose.getViewModel

@Composable
fun FavoritesScreen() {
    val viewModel: FavoritesViewModel = getViewModel()
    val state by viewModel.stateFlow.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()
        ActionsAppBar(
            title = "Favorites",
            leadingButtons = { BackButton() },
            modifier = Modifier.liftOnScroll(listState)
        )
        DrinkGrid(
            drinks = state.drinks,
            listState = listState,
            emptyMessage = "You don't have\nfavorites drinks yet",
            contentPadding = LocalWindowInsets.current.navigationBars.toPaddingValues()
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Preview("Dark Theme", widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FavoritesScreenPreview() {
    BarneeTheme {
        FavoritesScreen()
    }
}