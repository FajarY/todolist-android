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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    onPrimary = Color(0xFF2D5A32),      // slightly deeper green – still rich, better contrast on white
    onSecondary = Color(0xFF639A5F),    // medium muted green – still fresh, less “minty” than before
    onTertiary = Color(0xFF8BB582),     // softer, warm light green
    background = Color(0xFFF8F3E4),     // warm off‑white cream – softer on the eyes than pure yellow‑cream
    surface = Color.White,              // pure white cards – clean contrast
    onSurface = Color(0xFF1C1B1F),      // near‑black for body text (Accessibility AAA)
    onBackground = Color(0xFF424242)    // darker grey for labels like “Progress” – much better legibility
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