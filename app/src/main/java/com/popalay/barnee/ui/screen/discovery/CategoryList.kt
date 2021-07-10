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

package com.popalay.barnee.ui.screen.discovery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.popalay.barnee.R
import com.popalay.barnee.data.model.Category
import com.popalay.barnee.domain.Result
import com.popalay.barnee.ui.common.DEFAULT_COLUMNS
import com.popalay.barnee.ui.common.DefaultHorizontalItemPadding
import com.popalay.barnee.ui.common.DefaultItemShift
import com.popalay.barnee.ui.common.DefaultVerticalItemPadding
import com.popalay.barnee.ui.common.EmptyStateView
import com.popalay.barnee.ui.common.LoadingStateView
import com.popalay.barnee.ui.common.StateLayout
import com.popalay.barnee.ui.common.itemsInGridIndexed
import com.popalay.barnee.ui.common.plus
import com.popalay.barnee.ui.common.scrim
import com.popalay.barnee.ui.theme.DEFAULT_ASPECT_RATIO
import com.popalay.barnee.ui.theme.MediumSquircleShape
import com.popalay.barnee.ui.util.applyForImageUrl

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryGrid(
    categories: Result<List<Category>>,
    emptyMessage: String,
    onItemClick: (Category) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    StateLayout(
        value = categories,
        emptyState = {
            EmptyStateView(
                message = emptyMessage,
                modifier = modifier
            )
        },
        errorState = {
            EmptyStateView(
                message = emptyMessage,
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
                items = value,
                columns = DEFAULT_COLUMNS,
                contentPadding = fullContentPadding,
                horizontalItemPadding = DefaultHorizontalItemPadding,
                verticalItemPadding = DefaultVerticalItemPadding,
            ) { index, item ->
                CategoryListItem(
                    item,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.padding(top = if (index % 2 == 1 && value.size > 1) DefaultItemShift else 0.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryListItem(
    data: Category,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        elevation = 4.dp,
        shape = MediumSquircleShape,
        modifier = modifier.aspectRatio(DEFAULT_ASPECT_RATIO)
    ) {
        Box(
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Image(
                painter = rememberCoilPainter(
                    request = data.imageUrl,
                    requestBuilder = { size -> applyForImageUrl(data.imageUrl, size) },
                    fadeIn = true
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .scrim(listOf(Color.Transparent, Color.Black))
                    .padding(16.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = data.text,
                    style = MaterialTheme.typography.h2,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                        .padding(end = 8.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = null,
                    modifier = Modifier.rotate(180F)
                )
            }
        }
    }
}
