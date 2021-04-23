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

@Suppress("FunctionName")
fun SquircleShape(@FloatRange(from = 0.0, to = 1.0) allCurve: Float) =
    SquircleShape(allCurve, allCurve, allCurve, allCurve)

/**
 * curve == 0 - rectangle
 * curve == 1 - ellipse
 */
@Suppress("FunctionName")
fun SquircleShape(
    @FloatRange(from = 0.0, to = 1.0) curveTopLeft: Float,
    @FloatRange(from = 0.0, to = 1.0) curveTopRight: Float,
    @FloatRange(from = 0.0, to = 1.0) curveBottomLeft: Float,
    @FloatRange(from = 0.0, to = 1.0) curveBottomRight: Float,
) = GenericShape { size, _ ->
    moveTo(x = 0F, y = size.height * curveTopLeft)
    cubicTo(
        x1 = 0F, y1 = 0F,
        x2 = 0F, y2 = 0F,
        x3 = size.width * curveTopLeft, y3 = 0F
    )
    lineTo(x = size.width * (1 - curveTopRight), y = 0F)
    cubicTo(
        x1 = size.width, y1 = 0F,
        x2 = size.width, y2 = 0F,
        x3 = size.width, y3 = size.height * curveTopRight
    )
    lineTo(x = size.width, y = size.height * (1 - curveBottomRight))
    cubicTo(
        x1 = size.width, y1 = size.height,
        x2 = size.width, y2 = size.height,
        x3 = size.width * (1 - curveBottomRight), y3 = size.height
    )
    lineTo(x = size.width * curveBottomLeft, y = size.height)
    cubicTo(
        x1 = 0F, y1 = size.height,
        x2 = 0F, y2 = size.height,
        x3 = 0F, y3 = size.height * (1 - curveBottomLeft)
    )
}