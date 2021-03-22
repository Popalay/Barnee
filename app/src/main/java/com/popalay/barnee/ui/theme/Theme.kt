package com.popalay.barnee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Yellow,
    secondary = Yellow,
    surface = Grey,
    background = Grey,
    onPrimary = White,
    onSurface = White,
    onBackground = White
)

private val LightColorPalette = lightColors(
    primary = DarkYellow,
    secondary = DarkYellow,
    surface = White,
    background = White,
    onPrimary = Black,
    onSurface = Black,
    onBackground = Black
)

@Composable
fun BarneeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}