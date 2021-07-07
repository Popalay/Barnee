/*
 * Copyright (c) 2021 Denys Nykyforov
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun ActionsAppBar(
    title: String,
    modifier: Modifier = Modifier,
    leadingButtons: (@Composable RowScope.() -> Unit)? = null,
    trailingButtons: (@Composable RowScope.() -> Unit)? = null,
) {
    ActionsAppBar(
        AnnotatedString(title),
        modifier,
        leadingButtons,
        trailingButtons
    )
}

@Composable
fun ActionsAppBar(
    title: AnnotatedString,
    modifier: Modifier = Modifier,
    leadingButtons: (@Composable RowScope.() -> Unit)? = null,
    trailingButtons: (@Composable RowScope.() -> Unit)? = null,
) {
    ActionsAppBar(
        { Text(text = title) },
        modifier,
        leadingButtons,
        trailingButtons
    )
}

@Composable
fun ActionsAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingButtons: (@Composable RowScope.() -> Unit)? = null,
    trailingButtons: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp)
            .padding(start = if (leadingButtons != null) 0.dp else 16.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onBackground) {
            leadingButtons?.invoke(this)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.h2) {
                title()
            }
        }
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {
            trailingButtons?.invoke(this)
        }
    }
}
