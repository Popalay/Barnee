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

package com.popalay.barnee.ui.theme

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
val LargeSquircleShape = SquircleShape(0.15F)

@Suppress("FunctionName")
fun SquircleShape(allCurve: Float) = SquircleShape(allCurve, allCurve, allCurve, allCurve)

/**
 * curve == 0 - rectangle
 * curve == 1 - ellipse
 */
@Suppress("FunctionName")
fun SquircleShape(
    curveTopStart: Float,
    curveTopEnd: Float,
    curveBottomStart: Float,
    curveBottomEnd: Float,
) = GenericShape { size, _ ->
    moveTo(x = 0F, y = size.height * curveTopStart)
    cubicTo(
        x1 = 0F, y1 = 0F,
        x2 = 0F, y2 = 0F,
        x3 = size.width * curveTopStart, y3 = 0F
    )
    lineTo(x = size.width * (1 - curveTopEnd), y = 0F)
    cubicTo(
        x1 = size.width, y1 = 0F,
        x2 = size.width, y2 = 0F,
        x3 = size.width, y3 = size.height * curveTopEnd
    )
    lineTo(x = size.width, y = size.height * (1 - curveBottomEnd))
    cubicTo(
        x1 = size.width, y1 = size.height,
        x2 = size.width, y2 = size.height,
        x3 = size.width * (1 - curveBottomEnd), y3 = size.height
    )
    lineTo(x = size.width * curveBottomStart, y = size.height)
    cubicTo(
        x1 = 0F, y1 = size.height,
        x2 = 0F, y2 = size.height,
        x3 = 0F, y3 = size.height * (1 - curveBottomStart)
    )
}
