package com.popalay.barnee.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun Rating(
    rating: Int,
    modifier: Modifier = Modifier
) {
    val circlesFilled = (rating / 20F).roundToInt()
    val color = MaterialTheme.colors.onSurface
    val radius = 3.dp
    Box(modifier = modifier
        .drawBehind {
            val firstXOffset = (center.x - 5 * 3 * radius.toPx()) / 2
            repeat(5) {
                drawCircle(
                    color = color,
                    radius = radius.toPx(),
                    center = Offset(x = firstXOffset, y = 0F) + Offset(x = it * 3 * radius.toPx(), y = 0F),
                    alpha = if (it < circlesFilled) 1F else 0.5F
                )
            }
        }
    )
}