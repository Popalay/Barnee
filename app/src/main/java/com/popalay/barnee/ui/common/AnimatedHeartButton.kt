package com.popalay.barnee.ui.common

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.popalay.barnee.R.drawable

@Composable
fun AnimatedHeartButton(
    onToggle: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp
) {
    val transitionData = updateTransitionData(iconSize, isSelected)

    IconButton(
        onClick = onToggle,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = if (isSelected) drawable.ic_heart_filled else drawable.ic_heart),
            contentDescription = "Like",
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