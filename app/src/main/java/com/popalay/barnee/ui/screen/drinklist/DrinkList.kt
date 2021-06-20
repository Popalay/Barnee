package com.popalay.barnee.ui.screen.drinklist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.coil.rememberCoilPainter
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.drinkitem.DrinkItemAction.ToggleFavorite
import com.popalay.barnee.navigation.AppNavigation
import com.popalay.barnee.navigation.LocalNavController
import com.popalay.barnee.ui.common.AnimatedHeartButton
import com.popalay.barnee.ui.common.DEFAULT_COLUMNS
import com.popalay.barnee.ui.common.DefaultHorizontalItemPadding
import com.popalay.barnee.ui.common.DefaultItemShift
import com.popalay.barnee.ui.common.DefaultVerticalItemPadding
import com.popalay.barnee.ui.common.EmptyStateView
import com.popalay.barnee.ui.common.ErrorAndRetryStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.PageLoadingIndicator
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.itemsInGridIndexed
import com.popalay.barnee.ui.common.plus
import com.popalay.barnee.ui.common.scrim
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.ui.util.applyForImageUrl
import com.popalay.barnee.ui.util.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrinkGrid(
    drinks: LazyPagingItems<Drink>,
    emptyMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: DrinkItemViewModel = getViewModel()
    val navController = LocalNavController.current

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
                columns = DEFAULT_COLUMNS,
                contentPadding = fullContentPadding,
                horizontalItemPadding = DefaultHorizontalItemPadding,
                verticalItemPadding = DefaultVerticalItemPadding,
            ) { index, item ->
                item?.let {
                    DrinkListItem(
                        item,
                        onClick = { navController.navigate(AppNavigation.drink(item)) },
                        onDoubleClick = { viewModel.processAction(ToggleFavorite(item)) },
                        onHeartClick = { viewModel.processAction(ToggleFavorite(item)) },
                        modifier = Modifier.padding(top = if (index % 2 == 1 && value.itemCount > 1) DefaultItemShift else 0.dp)
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
    val viewModel: DrinkItemViewModel = getViewModel()
    val navController = LocalNavController.current

    BoxWithConstraints {
        LazyRow(
            contentPadding = contentPadding,
            modifier = modifier
        ) {
            itemsIndexed(data) { index, item ->
                DrinkListItem(
                    item,
                    onClick = { navController.navigate(AppNavigation.drink(item)) },
                    onDoubleClick = { viewModel.processAction(ToggleFavorite(item)) },
                    onHeartClick = { viewModel.processAction(ToggleFavorite(item)) },
                    modifier = Modifier.width(maxWidth / 3)
                )
                if (index != data.lastIndex) Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DrinkListItem(
    data: Drink,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},
    onHeartClick: () -> Unit = {}
) {
    Card(
        elevation = 4.dp,
        shape = MediumSquircleShape,
        modifier = modifier.aspectRatio(0.8F)
    ) {
        Box(
            modifier = Modifier.combinedClickable(onClick = onClick, onDoubleClick = onDoubleClick)
        ) {
            Image(
                painter = rememberCoilPainter(
                    request = data.displayImageUrl,
                    requestBuilder = { size -> applyForImageUrl(data.displayImageUrl, size) },
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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
                    text = data.displayRating,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary
                )
            }
            AnimatedHeartButton(
                onToggle = onHeartClick,
                isSelected = data.isFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            )
        }
    }
}
