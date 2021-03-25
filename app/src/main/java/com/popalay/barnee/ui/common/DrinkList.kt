package com.popalay.barnee.ui.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells.Fixed
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign.Center
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.popalay.barnee.data.model.Drink
import com.popalay.barnee.domain.Result
import com.popalay.barnee.ui.screen.navigation.Screen
import dev.chrisbanes.accompanist.coil.CoilImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrinkList(
    drinks: Result<List<Drink>>,
    navController: NavController,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
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
                    modifier
                        .clickable { navController.navigate(Screen.Drink(item.alias, item.name, item.displayImageUrl).route) }
                        .padding(4.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
            item { Spacer(modifier = Modifier.height(contentPadding.calculateBottomPadding())) }
        }
    }
}

@Composable
private fun DrinkListItem(data: Drink, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Column {
            CoilImage(
                data = data.displayImageUrl,
                fadeIn = true,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(0.7F)
                    .clip(RoundedCornerShape(4.dp))
            )
            Text(
                text = data.name,
                style = MaterialTheme.typography.h3,
                textAlign = Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Rating(
                rating = data.rating,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}