/*
 * Copyright (c) 2021 Denys Nykyforov
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

package com.popalay.barnee.ui.screen.collectionlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
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
import coil.compose.rememberImagePainter
import com.popalay.barnee.data.model.Collection
import com.popalay.barnee.data.model.ImageUrl
import com.popalay.barnee.domain.Result
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
import com.popalay.barnee.ui.theme.DEFAULT_ASPECT_RATIO
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.ui.util.applyForImageUrl
import com.popalay.barnee.ui.util.toIntSize

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionGrid(
    collections: Result<Set<Collection>>,
    emptyMessage: String,
    onRetry: () -> Unit,
    onItemClick: (Collection) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
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
                    onClick = { onItemClick(item) },
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
            modifier = Modifier.aspectRatio(DEFAULT_ASPECT_RATIO),
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
    BoxWithConstraints(modifier = modifier) {
        Image(
            painter = rememberImagePainter(
                data = image,
                builder = { applyForImageUrl(image, constraints.toIntSize()) },
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}
