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

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.popalay.barnee.ui.icons.HeartFilled
import com.popalay.barnee.ui.icons.HeartOutline

@Composable
fun AnimatedHeartButton(
    onToggle: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = 24.dp
) {
    val transitionData = updateTransitionData(iconSize, isSelected)

    IconButton(
        onClick = onToggle,
        enabled = enabled,
        modifier = modifier
    ) {
        Image(
            imageVector = if (isSelected) Icons.HeartFilled else Icons.HeartOutline,
            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.5F)),
            contentDescription = "Like",
            alpha = LocalContentAlpha.current,
            modifier = Modifier
                .size(transitionData.size)
                .offset(1.dp, 1.dp)
        )
        Image(
            imageVector = if (isSelected) Icons.HeartFilled else Icons.HeartOutline,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            contentDescription = "Like",
            alpha = LocalContentAlpha.current,
            modifier = Modifier.size(transitionData.size)
        )
    }
}

private class TransitionData(size: State<Dp>) {
    val size by size
}

@Composable
private fun updateTransitionData(actualSize: Dp, isSelected: Boolean): TransitionData {
    val transition = updateTransition(isSelected, label = "Heart button transition")
    val size = transition.animateDp(
        transitionSpec = {
            keyframes {
                durationMillis = 500
                actualSize * 1.3F at 100
                actualSize at 200
            }
        },
        label = "Heart button size"
    ) { state -> if (state) actualSize else actualSize }
    return remember(transition) { TransitionData(size) }
}
