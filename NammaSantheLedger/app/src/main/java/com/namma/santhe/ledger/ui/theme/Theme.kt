package com.namma.santhe.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warm earthy tones inspired by rural Karnataka market colours
val SantheOrange = Color(0xFFE8650A)
val SantheOrangeDark = Color(0xFFC4520A)
val SantheOrangeLight = Color(0xFFFFF0E6)
val SantheGreen = Color(0xFF2E7D32)
val SantheGreenLight = Color(0xFFE8F5E9)
val SantheRed = Color(0xFFC62828)
val SantheRedLight = Color(0xFFFFEBEE)
val SantheAmber = Color(0xFFFF8F00)
val SantheAmberLight = Color(0xFFFFF8E1)
val SantheBg = Color(0xFFFFFBF7)
val SantheSurface = Color(0xFFFFFFFF)
val SantheOnSurface = Color(0xFF1C1B1F)
val SantheOutline = Color(0xFFE0D9D2)
val SantheSubtext = Color(0xFF7B7068)

private val LightColorScheme = lightColorScheme(
    primary = SantheOrange,
    onPrimary = Color.White,
    primaryContainer = SantheOrangeLight,
    onPrimaryContainer = SantheOrangeDark,
    secondary = SantheGreen,
    onSecondary = Color.White,
    secondaryContainer = SantheGreenLight,
    onSecondaryContainer = SantheGreen,
    error = SantheRed,
    errorContainer = SantheRedLight,
    background = SantheBg,
    surface = SantheSurface,
    onBackground = SantheOnSurface,
    onSurface = SantheOnSurface,
    outline = SantheOutline,
    surfaceVariant = Color(0xFFF5EDE6),
    onSurfaceVariant = SantheSubtext
)

@Composable
fun NammaSantheTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme, // Always light – designed for outdoor market use
        typography = NammaSantheTypography,
        content = content
    )
}
