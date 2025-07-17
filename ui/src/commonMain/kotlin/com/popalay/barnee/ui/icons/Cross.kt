/*
 * Copyright (c) 2025 Denys Nykyforov
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

val Icons.Cross: ImageVector
    get() {
        if (_cross != null) {
            return _cross!!
        }
        _cross = Builder(
            name = "Cross",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            materialPath {
                moveTo(13.46f, 12.0f)
                lineTo(19.0f, 17.54f)
                verticalLineTo(19.0f)
                horizontalLineTo(17.54f)
                lineTo(12.0f, 13.46f)
                lineTo(6.46f, 19.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(17.54f)
                lineTo(10.54f, 12.0f)
                lineTo(5.0f, 6.46f)
                verticalLineTo(5.0f)
                horizontalLineTo(6.46f)
                lineTo(12.0f, 10.54f)
                lineTo(17.54f, 5.0f)
                horizontalLineTo(19.0f)
                verticalLineTo(6.46f)
                lineTo(13.46f, 12.0f)
                close()
            }
        }
            .build()
        return _cross!!
    }

private var _cross: ImageVector? = null
