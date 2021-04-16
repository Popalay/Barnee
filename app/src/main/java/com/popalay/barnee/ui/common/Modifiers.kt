package com.popalay.barnee.ui.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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