package com.github.kuya32.geocachingandroidcodingexercise2.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Green,
    background = DarkGreen,
    onBackground = White
)

@Composable
fun GeocachingAndroidCodingExercise2Theme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}