package com.example.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val LightColors = lightColorScheme(
    primary = BrightPurple,
    onPrimary = Color.White,
    primaryContainer = SoftLilac,
    onPrimaryContainer = BrightPurple,
    secondary = AquaTeal,
    onSecondary = Color.White,
    secondaryContainer = AquaTeal.copy(alpha = 0.2f),
    onSecondaryContainer = AquaTeal,
    tertiary = CoralOrange,
    onTertiary = Color.White,
    tertiaryContainer = CoralOrange.copy(alpha = 0.2f),
    onTertiaryContainer = CoralOrange,
    background = Color(0xFFFDF8FF),
    surface = Color(0xFFF9F2FF),
    surfaceVariant = Color(0xFFEFE2FF),
    onSurface = MidnightBlue,
    onSurfaceVariant = MidnightBlue.copy(alpha = 0.7f),
    outlineVariant = MidnightBlue.copy(alpha = 0.3f),
)

private val DarkColors = darkColorScheme(
    primary = SunshineYellow,
    onPrimary = MidnightBlue,
    primaryContainer = MidnightBlue,
    onPrimaryContainer = SunshineYellow,
    secondary = AquaTeal,
    onSecondary = MidnightBlue,
    secondaryContainer = MidnightBlue.copy(alpha = 0.7f),
    onSecondaryContainer = SunshineYellow,
    tertiary = CoralOrange,
    onTertiary = MidnightBlue,
    tertiaryContainer = MidnightBlue.copy(alpha = 0.6f),
    onTertiaryContainer = CoralOrange,
    background = MidnightBlue,
    surface = MidnightBlue.copy(alpha = 0.95f),
    surfaceVariant = MidnightBlue.copy(alpha = 0.75f),
    onSurface = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFFCED6E0),
    outlineVariant = Color(0xFF57606F),
)

private val RoundedShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun CalculatorTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = RoundedShapes,
        content = content
    )
}
