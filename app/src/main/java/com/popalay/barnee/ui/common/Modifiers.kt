package com.popalay.barnee.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
