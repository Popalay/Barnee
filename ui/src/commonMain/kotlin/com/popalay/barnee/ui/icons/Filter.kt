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

val Icons.Filter: ImageVector
    get() {
        if (_filter != null) {
            return _filter!!
        }
        _filter = Builder(
            name = "Filter",
            defaultWidth = 18.0.dp,
            defaultHeight = 18.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            materialPath {
                moveTo(8.0006f, 14.5963f)
                curveTo(8.8874f, 14.596f, 9.7492f, 14.9052f, 10.4503f, 15.4753f)
                curveTo(11.1515f, 16.0453f, 11.6523f, 16.8439f, 11.874f, 17.7453f)
                horizontalLineTo(22.9983f)
                curveTo(23.2482f, 17.7448f, 23.4891f, 17.8426f, 23.6738f, 18.0193f)
                curveTo(23.8584f, 18.196f, 23.9733f, 18.4389f, 23.9959f, 18.7002f)
                curveTo(24.0185f, 18.9614f, 23.9471f, 19.222f, 23.7957f, 19.4308f)
                curveTo(23.6444f, 19.6395f, 23.4241f, 19.7812f, 23.1783f, 19.8279f)
                lineTo(22.9983f, 19.8447f)
                lineTo(11.874f, 19.8468f)
                curveTo(11.6531f, 20.749f, 11.1526f, 21.5485f, 10.4513f, 22.1193f)
                curveTo(9.7501f, 22.6902f, 8.888f, 23.0f, 8.0006f, 23.0f)
                curveTo(7.1133f, 23.0f, 6.2511f, 22.6902f, 5.5499f, 22.1193f)
                curveTo(4.8487f, 21.5485f, 4.3482f, 20.749f, 4.1272f, 19.8468f)
                lineTo(1.0017f, 19.8447f)
                curveTo(0.7518f, 19.8452f, 0.5109f, 19.7474f, 0.3262f, 19.5707f)
                curveTo(0.1416f, 19.394f, 0.0267f, 19.1511f, 0.0041f, 18.8899f)
                curveTo(-0.0185f, 18.6286f, 0.0529f, 18.368f, 0.2043f, 18.1593f)
                curveTo(0.3556f, 17.9505f, 0.5759f, 17.8088f, 0.8217f, 17.7621f)
                lineTo(1.0017f, 17.7453f)
                horizontalLineTo(4.1272f)
                curveTo(4.3489f, 16.8439f, 4.8497f, 16.0453f, 5.5509f, 15.4753f)
                curveTo(6.252f, 14.9052f, 7.1138f, 14.596f, 8.0006f, 14.5963f)
                close()
                moveTo(15.9994f, 2.0f)
                curveTo(16.8862f, 1.9998f, 17.7479f, 2.309f, 18.4491f, 2.879f)
                curveTo(19.1503f, 3.4491f, 19.6511f, 4.2476f, 19.8728f, 5.1491f)
                horizontalLineTo(22.9983f)
                curveTo(23.2482f, 5.1486f, 23.4891f, 5.2463f, 23.6738f, 5.423f)
                curveTo(23.8584f, 5.5998f, 23.9733f, 5.8427f, 23.9959f, 6.1039f)
                curveTo(24.0185f, 6.3651f, 23.9471f, 6.6258f, 23.7957f, 6.8345f)
                curveTo(23.6444f, 7.0432f, 23.4241f, 7.1849f, 23.1783f, 7.2316f)
                lineTo(22.9983f, 7.2484f)
                lineTo(19.8728f, 7.2505f)
                curveTo(19.6518f, 8.1528f, 19.1513f, 8.9522f, 18.4501f, 9.5231f)
                curveTo(17.7489f, 10.0939f, 16.8867f, 10.4037f, 15.9994f, 10.4037f)
                curveTo(15.112f, 10.4037f, 14.2499f, 10.0939f, 13.5487f, 9.5231f)
                curveTo(12.8474f, 8.9522f, 12.3469f, 8.1528f, 12.126f, 7.2505f)
                lineTo(1.0017f, 7.2484f)
                curveTo(0.7518f, 7.2489f, 0.5109f, 7.1512f, 0.3262f, 6.9745f)
                curveTo(0.1416f, 6.7977f, 0.0267f, 6.5548f, 0.0041f, 6.2936f)
                curveTo(-0.0185f, 6.0324f, 0.0529f, 5.7717f, 0.2043f, 5.563f)
                curveTo(0.3556f, 5.3543f, 0.5759f, 5.2126f, 0.8217f, 5.1659f)
                lineTo(1.0017f, 5.1491f)
                horizontalLineTo(12.126f)
                curveTo(12.3477f, 4.2476f, 12.8485f, 3.4491f, 13.5497f, 2.879f)
                curveTo(14.2508f, 2.309f, 15.1126f, 1.9998f, 15.9994f, 2.0f)
                close()
            }
        }
            .build()
        return _filter!!
    }

private var _filter: ImageVector? = null
