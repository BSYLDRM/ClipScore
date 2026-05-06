package com.example.clipscore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    background = BrandBg,
    surface = BrandSurface,
    outline = BrandBorder,
    onPrimary = BrandText,
    onBackground = BrandText,
    onSurface = BrandText,
)

@Composable
fun ClipScoreTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}