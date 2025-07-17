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

val Icons.ChevronLeft: ImageVector
    get() {
        if (_chevronLeft != null) {
            return _chevronLeft!!
        }
        _chevronLeft = Builder(
            name = "ChevronLeft",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 19.0f,
            viewportHeight = 19.0f
        ).apply {
            materialPath {
                moveTo(12.9f, 17.269f)
                arcToRelative(1.026f, 1.026f, 0.0f, false, true, -0.727f, -0.302f)
                lineToRelative(-6.801f, -6.8f)
                arcToRelative(1.03f, 1.03f, 0.0f, false, true, 0.0f, -1.456f)
                lineToRelative(6.8f, -6.8f)
                arcToRelative(1.03f, 1.03f, 0.0f, false, true, 1.456f, 1.455f)
                lineTo(7.555f, 9.439f)
                lineToRelative(6.073f, 6.073f)
                arcTo(1.03f, 1.03f, 0.0f, false, true, 12.9f, 17.27f)
                close()
            }
        }
            .build()
        return _chevronLeft!!
    }

private var _chevronLeft: ImageVector? = null
