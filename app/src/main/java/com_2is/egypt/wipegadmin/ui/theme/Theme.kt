package com_2is.egypt.wipegadmin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = White,
    secondary = Rust300,
    background = Gray900,
    onPrimary = Gray900,
    onSecondary = Gray900,
    onBackground = Taupe100,
    onSurface = White800,
)

private val LightColorPalette = lightColors(
    primary = Gray900,
    secondary = Rust600,
    background = Taupe100,
    onPrimary = White,
    onSecondary = White,
    onBackground = Taupe800,
    onSurface = Gray800

)

@Composable
fun WipEgAdminTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
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