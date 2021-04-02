package com.popalay.barnee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Yellow,
    primaryVariant = Grey,
    secondary = Green,
    surface = DarkGrey,
    background = DarkGrey,
    onPrimary = White,
    onSurface = Black,
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

val Colors.shadow get() = Color(0xCC15181B)

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