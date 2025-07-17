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

package com.popalay.barnee.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.popalay.barnee.data.model.ImageUrl
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImagePainter

@Composable
fun AsyncImage(
    imageUrl: ImageUrl,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
) {
    BoxWithConstraints(modifier) {
        Image(
            painter = rememberImageUrlPainter(imageUrl),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            alpha = alpha,
            colorFilter = colorFilter,
            alignment = alignment,
            contentScale = contentScale
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.rememberImageUrlPainter(data: ImageUrl): Painter {
    val request = remember(data, constraints) {
        ImageRequest {
            data(data.scaledUrl(constraints.maxWidth to constraints.maxHeight))

            placeholderPainter {
                rememberImagePainter(data.url)
            }

            extra {
                set("key_url", data.url)
            }
        }
    }
    return rememberImagePainter(request)
}
