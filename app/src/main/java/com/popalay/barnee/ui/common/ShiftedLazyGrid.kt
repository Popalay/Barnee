package com.popalay.barnee.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.GridCells.Fixed
import androidx.compose.foundation.lazy.LazyGridScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShiftedLazyGrid(
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyGridScope.(itemContentPaddingResolver: (index: Int, size: Int) -> PaddingValues) -> Unit
) {
    LazyVerticalGrid(
        cells = Fixed(2),
        state = listState,
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        item { Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding())) }
        item { Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding())) }
        content { index, size ->
            PaddingValues(
                top = if (index % 2 == 1 && size > 1) 24.dp else 0.dp,
                start = 12.dp,
                end = 12.dp
            )
        }
        item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
        item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
    }
}