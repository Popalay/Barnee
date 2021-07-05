package com.popalay.barnee.ui.screen.collectionlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.domain.Result
import com.popalay.barnee.navigation.AppNavigation
import com.popalay.barnee.navigation.LocalNavController
import com.popalay.barnee.ui.common.DEFAULT_COLUMNS
import com.popalay.barnee.ui.common.DefaultHorizontalItemPadding
import com.popalay.barnee.ui.common.DefaultVerticalItemPadding
import com.popalay.barnee.ui.common.EmptyStateView
import com.popalay.barnee.ui.common.ErrorAndRetryStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.itemsInGridIndexed
import com.popalay.barnee.ui.common.plus
import com.popalay.barnee.ui.common.topShift
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.ui.util.applyForImageUrl

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionGrid(
    collections: Result<Set<Collection>>,
    emptyMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val navController = LocalNavController.current

    StateLayout(
        value = collections,
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
                items = value.toList(),
                columns = DEFAULT_COLUMNS,
                contentPadding = fullContentPadding,
                horizontalItemPadding = DefaultHorizontalItemPadding,
                verticalItemPadding = DefaultVerticalItemPadding,
            ) { index, item ->
                CollectionItem(
                    item,
                    onClick = { navController.navigate(AppNavigation.collection(item)) },
                    modifier = Modifier.topShift(index = index, size = value.size)
                )
            }
        }
    }
}

@Composable
fun CollectionItem(
    data: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Card(
            elevation = 4.dp,
            shape = MediumSquircleShape,
            modifier = Modifier.aspectRatio(0.8F),
        ) {
            CollectionCover(
                images = data.cover,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
        Text(
            text = data.name,
            style = MaterialTheme.typography.h4,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun CollectionCover(
    images: Set<ImageUrl>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CoverRow(
            images = images.take(2),
            modifier = Modifier.weight(1F)
        )
        if (images.size > 2) {
            Spacer(modifier = Modifier.height(2.dp))
            CoverRow(
                images = images.drop(2).take(2),
                modifier = Modifier.weight(1F)
            )
        }
    }
}

@Composable
private fun CoverRow(
    images: List<ImageUrl>,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        images.elementAtOrNull(0)?.let {
            CoverImage(
                image = it,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            )
        }
        images.elementAtOrNull(1)?.let {
            Spacer(modifier = Modifier.width(2.dp))
            CoverImage(
                image = it,
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun CoverImage(
    image: ImageUrl,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberCoilPainter(
            request = image,
            requestBuilder = { size -> applyForImageUrl(image, size) },
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}
