package dev.guruprasath.feeledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF1A3A6B)
val Green = Color(0xFF2E7D5B)
val Amber = Color(0xFFB4561A)
val Red = Color(0xFFB3261E)

private val LightColors = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7E3FA),
    onPrimaryContainer = Color(0xFF0B1F3F),
    secondary = Green,
    onSecondary = Color.White,
    tertiary = Amber,
    background = Color(0xFFF4F7FC),
    surface = Color(0xFFF4F7FC),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFA9C7FF),
    onPrimary = Color(0xFF0B1F3F),
    primaryContainer = Color(0xFF2A4A7E),
    secondary = Color(0xFF8FD6B3),
    tertiary = Color(0xFFFFB68A),
)

@Composable
fun FeeLedgerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}
