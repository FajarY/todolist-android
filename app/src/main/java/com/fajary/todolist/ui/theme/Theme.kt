package com.fajary.todolist.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    onPrimary = Color(0xFFA5D6A7),
    onSecondary = Color(0xFF81C784),
    onTertiary = Color(0xFFC8E6C9),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    onBackground = Color(0xFFB0B0B0)
)

private val LightColorScheme = lightColorScheme(
    onPrimary = Color(0xFF2D5A32),
    onSecondary = Color(0xFF639A5F),
    onTertiary = Color(0xFF8BB582),
    background = Color(0xFFF8F3E4),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    onBackground = Color(0xFF424242)
)

@Composable
fun TodolistappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ShapeScheme,
        content = content,
    )
}