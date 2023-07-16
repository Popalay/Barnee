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
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val Icons.HeartOutline: ImageVector
    get() {
        if (_done != null) {
            return _done!!
        }
        _done = materialIcon(name = "HeartOutline") {
            materialPath {
                moveTo(12.1f, 18.55f)
                lineTo(12f, 18.65f)
                lineTo(11.89f, 18.55f)
                curveTo(7.14f, 14.24f, 4f, 11.39f, 4f, 8.5f)
                curveTo(4f, 6.5f, 5.5f, 5f, 7.5f, 5f)
                curveTo(9.04f, 5f, 10.54f, 6f, 11.07f, 7.36f)
                horizontalLineTo(12.93f)
                curveTo(13.46f, 6f, 14.96f, 5f, 16.5f, 5f)
                curveTo(18.5f, 5f, 20f, 6.5f, 20f, 8.5f)
                curveTo(20f, 11.39f, 16.86f, 14.24f, 12.1f, 18.55f)
                moveTo(16.5f, 3f)
                curveTo(14.76f, 3f, 13.09f, 3.81f, 12f, 5.08f)
                curveTo(10.91f, 3.81f, 9.24f, 3f, 7.5f, 3f)
                curveTo(4.42f, 3f, 2f, 5.41f, 2f, 8.5f)
                curveTo(2f, 12.27f, 5.4f, 15.36f, 10.55f, 20.03f)
                lineTo(12f, 21.35f)
                lineTo(13.45f, 20.03f)
                curveTo(18.6f, 15.36f, 22f, 12.27f, 22f, 8.5f)
                curveTo(22f, 5.41f, 19.58f, 3f, 16.5f, 3f)
                close()
            }
        }
        return _done!!
    }

private var _done: ImageVector? = null
