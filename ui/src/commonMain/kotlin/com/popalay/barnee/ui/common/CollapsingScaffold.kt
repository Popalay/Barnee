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

import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SwipeableState
import androidx.compose.material.contentColorFor
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import com.popalay.barnee.ui.common.CollapsingScaffoldValue.Collapsed
import com.popalay.barnee.ui.common.CollapsingScaffoldValue.Expanded
import com.popalay.barnee.ui.common.CollapsingScaffoldValue.values

@Composable
fun rememberCollapsingScaffoldState(
    minHeight: Float,
    maxHeight: Float,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    initialValue: CollapsingScaffoldValue = Expanded
): CollapsingScaffoldState = rememberSaveable(saver = CollapsingScaffoldState.Saver(scaffoldState)) {
    CollapsingScaffoldState(
        minHeightPx = minHeight,
        maxHeightPx = maxHeight,
        scaffoldState = scaffoldState,
        initialValue = initialValue
    )
}

enum class CollapsingScaffoldValue {
    Collapsed,
    Expanded
}

@OptIn(ExperimentalMaterialApi::class)
@Stable
class CollapsingScaffoldState(
    val minHeightPx: Float,
    val maxHeightPx: Float,
    val scaffoldState: ScaffoldState,
    initialValue: CollapsingScaffoldValue
) : SwipeableState<CollapsingScaffoldValue>(
    initialValue = initialValue,
    animationSpec = TweenSpec()
) {
    val fraction: Float by derivedStateOf { offset.value / maxOffset }
    val maxOffset: Float get() = -maxHeightPx + minHeightPx
    val topBarOffset by derivedStateOf { offset.value }
    val contentOffset by derivedStateOf { maxHeightPx + offset.value }

    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection

    companion object {
        @Suppress("FunctionName")
        fun Saver(scaffoldState: ScaffoldState): Saver<CollapsingScaffoldState, *> = listSaver(
            save = { listOf(it.minHeightPx, it.maxHeightPx, it.currentValue.ordinal) },
            restore = { list ->
                CollapsingScaffoldState(
                    minHeightPx = list[0].toFloat(),
                    maxHeightPx = list[1].toFloat(),
                    scaffoldState = scaffoldState,
                    initialValue = values()[list[2].toInt()]
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollapsingScaffold(
    state: CollapsingScaffoldState,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerGesturesEnabled: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    drawerScrimColor: Color = DrawerDefaults.scrimColor,
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    isEnabled: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        scaffoldState = state.scaffoldState,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        drawerContent = drawerContent,
        drawerGesturesEnabled = drawerGesturesEnabled,
        drawerShape = drawerShape,
        drawerElevation = drawerElevation,
        drawerBackgroundColor = drawerBackgroundColor,
        drawerContentColor = drawerContentColor,
        drawerScrimColor = drawerScrimColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        modifier = modifier
    ) { contentPadding ->
        val anchors = mapOf(
            state.maxOffset to Collapsed,
            0F to Expanded
        )
        val topPadding by with(LocalDensity.current) {
            remember(state.contentOffset) { derivedStateOf { state.contentOffset.toDp() } }
        }
        val swipeable = Modifier
            .nestedScroll(state.nestedScrollConnection)
            .swipeable(
                state = state,
                anchors = anchors,
                enabled = isEnabled,
                orientation = Orientation.Vertical,
                resistance = null
            )

        Box(swipeable) {
            Column {
                Spacer(modifier = Modifier.height(topPadding))
                content(contentPadding)
            }
            topBar()
        }
    }
}

@ExperimentalMaterialApi
internal val <T> SwipeableState<T>.PreUpPostDownNestedScrollConnection: NestedScrollConnection
    get() = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.toFloat()
            return if (delta < 0 && source == NestedScrollSource.Drag) {
                performDrag(delta).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset = if (source == NestedScrollSource.Drag) {
            performDrag(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val toFling = Offset(available.x, available.y).toFloat()
            return if (toFling < 0 && offset.value > Float.NEGATIVE_INFINITY) {
                performFling(velocity = toFling)
                available
            } else {
                Velocity.Zero
            }
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            performFling(velocity = Offset(available.x, available.y).toFloat())
            return available
        }

        private fun Float.toOffset(): Offset = Offset(0f, this)

        private fun Offset.toFloat(): Float = this.y
    }
