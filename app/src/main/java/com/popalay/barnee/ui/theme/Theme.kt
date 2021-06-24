package com.popalay.barnee.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Yellow,
    primaryVariant = DarkYellow,
    secondary = Green,
    surface = DarkGrey,
    background = DarkGrey,
    onPrimary = DarkGrey,
    onSurface = White,
    onBackground = White
)

@Composable
fun BarneeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
