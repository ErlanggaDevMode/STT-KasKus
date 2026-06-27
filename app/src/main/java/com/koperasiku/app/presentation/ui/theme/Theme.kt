package com.koperasiku.app.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = KoperasiGreenLight,
    secondary = KoperasiGold,
    tertiary = KoperasiBlue,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = KoperasiRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = KoperasiGreen,
    secondary = KoperasiGold,
    tertiary = KoperasiBlue,
    background = SurfaceLight,
    surface = SurfaceCard,
    error = KoperasiRed,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun KopkustTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = KoperasiTypography,
        content = content
    )
}
