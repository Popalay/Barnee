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

package com.popalay.barnee.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.unit.dp

val Icons.Bartender: ImageVector
    get() {
        if (_bartender != null) {
            return _bartender!!
        }
        _bartender = Builder(
            name = "Bartender",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 236.0f,
            viewportHeight = 256.0f
        ).apply {
            materialPath {
                moveTo(2.0f, 118.0f)
                verticalLineToRelative(27.0f)
                horizontalLineToRelative(23.0f)
                verticalLineToRelative(109.0f)
                horizontalLineToRelative(185.0f)
                verticalLineTo(145.0f)
                horizontalLineToRelative(24.0f)
                verticalLineToRelative(-27.0f)
                horizontalLineTo(2.0f)
                close()
                moveTo(109.73f, 229.99f)
                curveToRelative(0.74f, 0.07f, 1.34f, 0.82f, 1.26f, 1.63f)
                curveToRelative(-0.07f, 0.82f, -0.74f, 1.27f, -1.63f, 1.27f)
                horizontalLineTo(80.92f)
                curveToRelative(-0.89f, 0.0f, -1.56f, -0.45f, -1.64f, -1.27f)
                curveToRelative(-0.07f, -0.81f, 0.45f, -1.56f, 1.27f, -1.63f)
                lineToRelative(11.73f, -1.48f)
                verticalLineToRelative(-20.13f)
                curveToRelative(-22.43f, -6.83f, -9.95f, -32.01f, -7.5f, -38.69f)
                horizontalLineToRelative(20.72f)
                curveToRelative(2.45f, 6.68f, 14.93f, 31.86f, -7.5f, 38.69f)
                verticalLineToRelative(20.13f)
                lineTo(109.73f, 229.99f)
                close()
                moveTo(156.89f, 230.14f)
                curveToRelative(0.0f, 1.48f, -1.26f, 2.75f, -2.75f, 2.75f)
                horizontalLineToRelative(-27.03f)
                curveToRelative(-1.49f, 0.0f, -2.75f, -1.19f, -2.75f, -2.75f)
                verticalLineToRelative(-49.68f)
                curveToRelative(0.0f, -6.46f, 4.53f, -11.88f, 10.55f, -13.37f)
                verticalLineToRelative(-22.2f)
                horizontalLineToRelative(11.51f)
                verticalLineToRelative(22.2f)
                curveToRelative(6.01f, 1.49f, 10.47f, 6.91f, 10.47f, 13.37f)
                verticalLineTo(230.14f)
                close()
                moveTo(127.78f, 214.77f)
                horizontalLineToRelative(25.69f)
                verticalLineToRelative(-25.7f)
                horizontalLineToRelative(-25.69f)
                verticalLineTo(214.77f)
                close()
                moveTo(146.0f, 52.0f)
                horizontalLineTo(89.0f)
                curveToRelative(-15.59f, 0.0f, -28.0f, 13.41f, -28.0f, 29.0f)
                verticalLineToRelative(32.0f)
                horizontalLineToRelative(20.0f)
                verticalLineTo(86.0f)
                curveToRelative(0.0f, -1.71f, 1.29f, -3.0f, 3.0f, -3.0f)
                reflectiveCurveToRelative(3.0f, 1.29f, 3.0f, 3.0f)
                verticalLineToRelative(27.0f)
                horizontalLineToRelative(61.0f)
                verticalLineTo(86.0f)
                curveToRelative(0.0f, -1.71f, 1.29f, -3.0f, 3.0f, -3.0f)
                reflectiveCurveToRelative(3.0f, 1.29f, 3.0f, 3.0f)
                verticalLineToRelative(27.0f)
                horizontalLineToRelative(20.0f)
                verticalLineTo(81.0f)
                curveTo(174.0f, 65.51f, 161.49f, 52.0f, 146.0f, 52.0f)
                close()
                moveTo(117.5f, 2.0f)
                curveToRelative(-12.47f, 0.0f, -22.63f, 10.16f, -22.63f, 22.63f)
                curveToRelative(0.0f, 12.46f, 10.06f, 22.62f, 22.63f, 22.62f)
                curveToRelative(12.37f, 0.0f, 22.62f, -10.16f, 22.62f, -22.62f)
                curveTo(140.12f, 12.16f, 129.97f, 2.0f, 117.5f, 2.0f)
                close()
            }
        }
            .build()
        return _bartender!!
    }

private var _bartender: ImageVector? = null
