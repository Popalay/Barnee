/*
 * Copyright (c) 2026 Denys Nykyforov
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

package com.popalay.barnee.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch

val LocalAppBottomSheetNavigator = staticCompositionLocalOf<AppBottomSheetNavigator> {
    error("No AppBottomSheetNavigator provided")
}

@OptIn(ExperimentalMaterialApi::class)
class AppBottomSheetNavigator(
    val sheetState: ModalBottomSheetState,
    private val onShow: (Screen) -> Unit,
    private val onHide: () -> Unit,
) {
    val isVisible: Boolean get() = sheetState.isVisible

    fun show(screen: Screen) = onShow(screen)
    fun hide() = onHide()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppBottomSheetNavigator(
    sheetElevation: androidx.compose.ui.unit.Dp = 1.dp,
    sheetBackgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colors.background,
    sheetContentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colors.onBackground,
    scrimColor: androidx.compose.ui.graphics.Color = MaterialTheme.colors.background.copy(alpha = 0.7F),
    content: @Composable (AppBottomSheetNavigator) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf<Screen?>(null) }

    val navigator = remember(sheetState) {
        AppBottomSheetNavigator(
            sheetState = sheetState,
            onShow = { screen ->
                currentScreen = screen
                scope.launch { sheetState.show() }
            },
            onHide = {
                scope.launch { sheetState.hide() }
            },
        )
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetElevation = sheetElevation,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        scrimColor = scrimColor,
        sheetContent = {
            currentScreen?.Content() ?: Spacer(Modifier.height(1.dp))
        },
    ) {
        CompositionLocalProvider(LocalAppBottomSheetNavigator provides navigator) {
            content(navigator)
        }
    }
}
