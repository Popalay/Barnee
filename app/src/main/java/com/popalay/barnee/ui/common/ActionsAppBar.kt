package com.popalay.barnee.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
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
        {
            Text(
                text = title,
                style = MaterialTheme.typography.h2,
            )
        },
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
        leadingButtons?.invoke(this)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            title()
        }
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.primary) {
            trailingButtons?.invoke(this)
        }
    }
}