package com.popalay.barnee.ui.screen.drinklist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.GridCells.Fixed
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign.Start
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.google.accompanist.coil.CoilImage
import com.popalay.barnee.R
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.Result
import com.popalay.barnee.domain.drinklist.DrinkListAction.ToggleFavorite
import com.popalay.barnee.ui.common.EmptyStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.scrim
import com.popalay.barnee.ui.screen.navigation.LocalNavController
import com.popalay.barnee.ui.screen.navigation.Screen
import com.popalay.barnee.ui.theme.backgroundVariant
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrinkGrid(
    drinks: Result<List<Drink>>,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: DrinkListViewModel = getViewModel()
    val navController = LocalNavController.current

    StateLayout(
        value = drinks,
        emptyState = { EmptyStateView(message = emptyMessage) },
        errorState = { EmptyStateView(message = emptyMessage) },
        loadingState = { LoadingStateView() }
    ) { value ->
        LazyVerticalGrid(
            cells = Fixed(2),
            contentPadding = PaddingValues(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding())) }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateTopPadding())) }
            itemsIndexed(value) { index, item ->
                DrinkListItem(
                    item,
                    onClick = { navController.navigate(Screen.Drink(item.alias, item.displayName, item.displayImageUrl).route) },
                    onDoubleClick = { viewModel.processAction(ToggleFavorite(item.alias)) },
                    onHeartClick = { viewModel.processAction(ToggleFavorite(item.alias)) },
                    modifier = modifier
                        .padding(top = if (index % 2 == 0 && value.size > 1) 24.dp else 0.dp)
                        .padding(start = 12.dp, end = 12.dp, bottom = 0.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
        }
    }
}

@Composable
fun DrinkHorizontalList(
    data: List<Drink>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val viewModel: DrinkListViewModel = getViewModel()
    val navController = LocalNavController.current

    BoxWithConstraints {
        LazyRow(contentPadding = contentPadding) {
            itemsIndexed(data) { index, item ->
                DrinkListItem(
                    item,
                    onClick = { navController.navigate(Screen.Drink(item.alias, item.displayName, item.displayImageUrl).route) },
                    onDoubleClick = { viewModel.processAction(ToggleFavorite(item.alias)) },
                    onHeartClick = { viewModel.processAction(ToggleFavorite(item.alias)) },
                    modifier = modifier.width(maxWidth / 3)
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
        modifier = modifier.aspectRatio(0.8F)
    ) {
        Box(
            modifier = Modifier.combinedClickable(onClick = onClick, onDoubleClick = onDoubleClick)
        ) {
            CoilImage(
                data = data.displayImageUrl,
                fadeIn = true,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                loading = { Box(modifier = Modifier.background(MaterialTheme.colors.backgroundVariant)) },
                modifier = Modifier.fillMaxSize()
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .scrim(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7F)))
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = data.displayName,
                    style = MaterialTheme.typography.h2,
                    textAlign = Start,
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.7F)
                        .padding(end = 8.dp)
                )
                Text(
                    text = data.displayRating,
                    style = MaterialTheme.typography.h3,
                    color = MaterialTheme.colors.primary
                )
            }
            IconButton(
                onClick = onHeartClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(id = if (data.isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart),
                    contentDescription = "Like"
                )
            }
        }
    }
}