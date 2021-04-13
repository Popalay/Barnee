package com.popalay.barnee.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(12.dp)
)

val MediumSquircleShape = SquircleShape(0.25F)

/**
 * curve == 0 - rectangle
 * curve == 1 - ellipse
 */
@Suppress("FunctionName")
fun SquircleShape(@FloatRange(from = 0.0, to = 1.0) curve: Float) = GenericShape { size, _ ->
    val oppositeCurve = 1 - curve

    moveTo(x = 0F, y = size.height * curve)
    cubicTo(
        x1 = 0F, y1 = 0F,
        x2 = 0F, y2 = 0F,
        x3 = size.width * curve, y3 = 0F
    )
    lineTo(x = size.width * oppositeCurve, y = 0F)
    cubicTo(
        x1 = size.width, y1 = 0F,
        x2 = size.width, y2 = 0F,
        x3 = size.width, y3 = size.height * curve
    )
    lineTo(x = size.width, y = size.height * oppositeCurve)
    cubicTo(
        x1 = size.width, y1 = size.height,
        x2 = size.width, y2 = size.height,
        x3 = size.width * oppositeCurve, y3 = size.height
    )
    lineTo(x = size.width * curve, y = size.height)
    cubicTo(
        x1 = 0F, y1 = size.height,
        x2 = 0F, y2 = size.height,
        x3 = 0F, y3 = size.height * oppositeCurve
    )
}