package com.popalay.barnee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
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

private val LightColorPalette = lightColors(
    primary = DarkYellow,
    primaryVariant = Grey,
    secondary = DarkYellow,
    surface = White,
    background = White,
    onPrimary = Black,
    onSurface = Black,
    onBackground = Black
)

val Colors.backgroundVariant get() = Grey

@Composable
fun BarneeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}