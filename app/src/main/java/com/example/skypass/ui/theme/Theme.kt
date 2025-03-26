// Theme.kt
package com.example.skypass.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Import Typography and Shapes from your own package
import com.example.skypass.ui.theme.Typography
import com.example.skypass.ui.theme.Shapes

private val LightColors = lightColorScheme(
    primary = Color(0xFF3F51B5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = Color(0xFF001159),
    secondary = Color(0xFFFF9800),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFEACC),
    onSecondaryContainer = Color(0xFF2B1700),
    tertiary = Color(0xFF00BCD4),
    onTertiary = Color.White,
    error = Color(0xFFB00020),
    onError = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBBC3FF),
    onPrimary = Color(0xFF001D93),
    primaryContainer = Color(0xFF0027CA),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFFFB84C),
    onSecondary = Color(0xFF462A00),
    secondaryContainer = Color(0xFF643E00),
    onSecondaryContainer = Color(0xFFFFDDB5),
    tertiary = Color(0xFF86EAFF),
    onTertiary = Color(0xFF00505D),
    error = Color(0xFFCF6679),
    onError = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

@Composable
fun SkyPassTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}