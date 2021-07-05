package com.popalay.barnee.ui.screen.collectionlist

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.popalay.barnee.domain.collectionlist.CollectionListState
import com.popalay.barnee.ui.common.ActionsAppBar
import com.popalay.barnee.ui.common.BackButton
import com.popalay.barnee.ui.common.liftOnScroll
import com.popalay.barnee.ui.theme.BarneeTheme
import com.popalay.barnee.ui.util.collectAsStateWithLifecycle
import org.koin.androidx.compose.getViewModel

@Composable
fun CollectionListScreen() {
    CollectionListScreen(getViewModel())
}

@Composable
fun CollectionListScreen(viewModel: CollectionListViewModel) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    CollectionListScreen(state)
}

@Composable
fun CollectionListScreen(state: CollectionListState) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listState = rememberLazyListState()

        ActionsAppBar(
            title = "Collections",
            modifier = Modifier.liftOnScroll(listState),
            leadingButtons = { BackButton() }
        )
        CollectionGrid(
            collections = state.collections,
            listState = listState,
            emptyMessage = "You don't have any collections yet\nstart adding them by clicking the ♥ button",
            onRetry = { },
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
        CollectionListScreen(CollectionListState())
    }
}
