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

package com.popalay.barnee.ui.screen.drinklist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.ui.common.AsyncImage
import com.popalay.barnee.domain.drinkitem.DrinkItemAction.ToggleFavorite
import com.popalay.barnee.domain.drinkitem.DrinkItemStateMachine
import com.popalay.barnee.domain.navigation.AppScreens
import com.popalay.barnee.domain.navigation.NavigateToAction
import com.popalay.barnee.ui.common.AnimatedHeartButton
import com.popalay.barnee.ui.common.DefaultColumns
import com.popalay.barnee.ui.common.DefaultHorizontalItemPadding
import com.popalay.barnee.ui.common.DefaultVerticalItemPadding
import com.popalay.barnee.ui.common.EmptyStateView
import com.popalay.barnee.ui.common.ErrorAndRetryStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.PageLoadingIndicator
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.itemsInGridIndexed
import com.popalay.barnee.ui.common.plus
import com.popalay.barnee.ui.common.scrim
import com.popalay.barnee.ui.common.topShift
import com.popalay.barnee.ui.theme.DefaultAspectRatio
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.util.displayImageUrl
import com.popalay.barnee.util.displayName
import com.popalay.barnee.util.displayRating
import com.popalay.barnee.util.inCollections
import com.popalay.barnee.util.isGenerated
import com.popalay.barnee.util.toMinimumData
import org.koin.compose.koinInject

@Composable
fun DrinkGrid(
    drinks: LazyPagingItems<Drink>,
    emptyMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val stateMachine: DrinkItemStateMachine = koinInject()

    StateLayout(
        value = drinks,
        emptyState = {
            EmptyStateView(
                message = emptyMessage,
                modifier = modifier
            )
        },
        errorState = {
            ErrorAndRetryStateView(
                onRetry = onRetry,
                modifier = modifier
            )
        },
        loadingState = { LoadingStateView(modifier = modifier) }
    ) { value ->
        val fullContentPadding = PaddingValues(16.dp) + contentPadding
        LazyColumn(
            state = listState,
            modifier = modifier
        ) {
            itemsInGridIndexed(
                lazyPagingItems = value,
                columns = DefaultColumns,
                contentPadding = fullContentPadding,
                horizontalItemPadding = DefaultHorizontalItemPadding,
                verticalItemPadding = DefaultVerticalItemPadding,
            ) { index, item ->
                item?.let {
                    DrinkListItem(
                        item,
                        onClick = { stateMachine.dispatch(NavigateToAction(AppScreens.Drink(item.toMinimumData()))) },
                        onDoubleClick = { stateMachine.dispatch(ToggleFavorite(item)) },
                        onHeartClick = { stateMachine.dispatch(ToggleFavorite(item)) },
                        modifier = Modifier.topShift(index = index, size = value.itemCount)
                    )
                }
            }
            if (value.loadState.append == LoadState.Loading) {
                item { PageLoadingIndicator() }
            }
        }
    }
}

@Composable
fun DrinkHorizontalList(
    data: List<Drink>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val stateMachine: DrinkItemStateMachine = koinInject()

    BoxWithConstraints {
        LazyRow(
            contentPadding = contentPadding,
            modifier = modifier
        ) {
            itemsIndexed(data) { index, item ->
                DrinkListItem(
                    item,
                    onClick = { stateMachine.dispatch(NavigateToAction(AppScreens.Drink(item.toMinimumData()))) },
                    onDoubleClick = { stateMachine.dispatch(ToggleFavorite(item)) },
                    onHeartClick = { stateMachine.dispatch(ToggleFavorite(item)) },
                    modifier = Modifier.width(maxWidth / 3)
                )
                if (index != data.lastIndex) Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrinkListItem(
    data: Drink,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},
    onHeartClick: () -> Unit = {}
) {
    Card(
        elevation = 4.dp,
        shape = MediumSquircleShape,
        modifier = modifier.aspectRatio(DefaultAspectRatio)
    ) {
        Box(modifier = Modifier.combinedClickable(onClick = onClick, onDoubleClick = onDoubleClick)) {
            AsyncImage(data.displayImageUrl)
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .scrim(listOf(Color.Transparent, Color.Black.copy(alpha = 0.9F)))
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = data.displayName,
                    style = MaterialTheme.typography.h2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.7F)
                        .weight(1F)
                        .padding(end = 8.dp)
                )
                Text(
                    text = if (data.isGenerated) "AI ✨" else data.displayRating,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary
                )
            }
            if (!data.isGenerated) {
                AnimatedHeartButton(
                    onToggle = onHeartClick,
                    isSelected = data.inCollections,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }
    }
}
