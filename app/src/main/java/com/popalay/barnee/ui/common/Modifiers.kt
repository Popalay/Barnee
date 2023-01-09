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

package com.popalay.barnee.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A [Modifier] which draws a vertical gradient
 */
fun Modifier.scrim(colors: List<Color>): Modifier = drawWithContent {
    drawRect(Brush.verticalGradient(colors))
    drawContent()
}

fun Modifier.liftOnScroll(listState: LazyListState): Modifier = graphicsLayer {
    shadowElevation = if (listState.firstVisibleItemScrollOffset != 0) 1.dp.toPx() else 0F
}

@Composable
operator fun PaddingValues.plus(another: PaddingValues) = with(LocalLayoutDirection.current) {
    PaddingValues(
        start = calculateStartPadding(this) + another.calculateStartPadding(this),
        end = calculateEndPadding(this) + another.calculateEndPadding(this),
        top = calculateTopPadding() + another.calculateTopPadding(),
        bottom = calculateBottomPadding() + another.calculateBottomPadding(),
    )
}

fun Modifier.drawBadge(
    color: Color,
    radius: Dp
): Modifier = drawWithContent {
    val radiusPx = radius.toPx()
    drawContent()
    drawCircle(
        color = color,
        radius = radiusPx,
        center = Offset(size.width, size.height)
    )
}

fun Modifier.topShift(
    index: Int,
    size: Int
) = this.padding(top = if (index % 2 == 1 && size > 1) DefaultItemShift else 0.dp)
