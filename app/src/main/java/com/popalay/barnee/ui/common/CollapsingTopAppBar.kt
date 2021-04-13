package com.popalay.barnee.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun CollapsingScaffold(
    minHeight: Dp,
    maxHeight: Dp,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    appBarContent: @Composable (collapsedFraction: Float, offset: IntOffset) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val toolbarHeightPx = with(LocalDensity.current) { maxHeight.roundToPx().toFloat() }
    val collapsedToolbarHeightPx = with(LocalDensity.current) { minHeight.roundToPx().toFloat() }
    val toolbarMaxOffsetHeightPx = remember { -toolbarHeightPx + collapsedToolbarHeightPx }
    var toolbarOffsetHeightPx by rememberSaveable { mutableStateOf(0f) }
    val nestedScrollConnection = remember(isEnabled) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isEnabled) {
                    val delta = available.y
                    val newOffset = toolbarOffsetHeightPx + delta
                    toolbarOffsetHeightPx = newOffset.coerceIn(toolbarMaxOffsetHeightPx, 0f)
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        content(PaddingValues(top = maxHeight))
        val fraction = -toolbarOffsetHeightPx / (toolbarHeightPx - collapsedToolbarHeightPx)
        appBarContent(fraction, IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt()))
    }
}