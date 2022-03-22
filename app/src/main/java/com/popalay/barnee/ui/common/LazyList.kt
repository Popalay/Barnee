/*
 * Copyright (c) 2022 Denys Nykyforov
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

package com.popalay.barnee.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

const val DEFAULT_COLUMNS = 2
val DefaultItemShift = 24.dp
val DefaultHorizontalItemPadding = 24.dp
val DefaultVerticalItemPadding = 0.dp

fun LazyListScope.itemSpacer(height: Dp) {
    item {
        Spacer(Modifier.height(height).fillParentMaxWidth())
    }
}

val <T : Any> LazyPagingItems<T>.isUninitialized
    get() = itemCount == 0 &&
            loadState.refresh == LoadState.NotLoading(endOfPaginationReached = false) &&
            loadState.append == LoadState.NotLoading(endOfPaginationReached = false) &&
            loadState.prepend == LoadState.NotLoading(endOfPaginationReached = false)

/**
 * Displays a 'fake' grid using [LazyColumn]'s DSL. It's fake in that we just we add individual
 * column items, with a inner fake row.
 */
fun <T : Any> LazyListScope.itemsInGridIndexed(
    items: List<T>,
    columns: Int,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalItemPadding: Dp = 0.dp,
    verticalItemPadding: Dp = 0.dp,
    itemContent: @Composable (index: Int, item: T) -> Unit
) {
    val rows = when {
        items.size % columns == 0 -> items.size / columns
        else -> items.size / columns + 1
    }

    for (row in 0 until rows) {
        if (row == 0) itemSpacer(contentPadding.calculateTopPadding())

        item {
            val layoutDirection = LocalLayoutDirection.current

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = contentPadding.calculateStartPadding(layoutDirection),
                        end = contentPadding.calculateEndPadding(layoutDirection)
                    )
            ) {
                for (column in 0 until columns) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = row * columns + column
                        if (index < items.size) {
                            itemContent(index, items[index])
                        }
                    }
                    if (column < columns - 1) {
                        Spacer(modifier = Modifier.width(horizontalItemPadding))
                    }
                }
            }
        }

        if (row < rows - 1) {
            itemSpacer(verticalItemPadding)
        } else {
            itemSpacer(contentPadding.calculateBottomPadding())
        }
    }
}

fun <T : Any> LazyListScope.itemsInGridIndexed(
    lazyPagingItems: LazyPagingItems<T>,
    columns: Int,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalItemPadding: Dp = 0.dp,
    verticalItemPadding: Dp = 0.dp,
    itemContent: @Composable (index: Int, item: T?) -> Unit
) {
    val rows = when {
        lazyPagingItems.itemCount % columns == 0 -> lazyPagingItems.itemCount / columns
        else -> lazyPagingItems.itemCount / columns + 1
    }

    for (row in 0 until rows) {
        if (row == 0) itemSpacer(contentPadding.calculateTopPadding())

        item {
            val layoutDirection = LocalLayoutDirection.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = contentPadding.calculateStartPadding(layoutDirection),
                        end = contentPadding.calculateEndPadding(layoutDirection)
                    )
            ) {
                for (column in 0 until columns) {
                    Box(modifier = Modifier.weight(1f)) {
                        val index = row * columns + column
                        if (index < lazyPagingItems.itemCount) {
                            val item = lazyPagingItems[index]
                            itemContent(index, item)
                        }
                    }
                    if (column < columns - 1) {
                        Spacer(modifier = Modifier.width(horizontalItemPadding))
                    }
                }
            }
        }

        if (row < rows - 1) {
            itemSpacer(verticalItemPadding)
        } else {
            itemSpacer(contentPadding.calculateBottomPadding())
        }
    }
}

fun <T : Any> LazyListScope.itemsInGrid(
    items: List<T>,
    columns: Int,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalItemPadding: Dp = 0.dp,
    verticalItemPadding: Dp = 0.dp,
    itemContent: @Composable (item: T) -> Unit
) {
    itemsInGridIndexed(
        items,
        columns,
        contentPadding,
        horizontalItemPadding,
        verticalItemPadding
    ) { _, item -> itemContent(item) }
}

fun <T : Any> LazyListScope.itemsInGrid(
    lazyPagingItems: LazyPagingItems<T>,
    columns: Int,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalItemPadding: Dp = 0.dp,
    verticalItemPadding: Dp = 0.dp,
    itemContent: @Composable (item: T?) -> Unit
) {
    itemsInGridIndexed(
        lazyPagingItems,
        columns,
        contentPadding,
        horizontalItemPadding,
        verticalItemPadding
    ) { _, item -> itemContent(item) }
}
