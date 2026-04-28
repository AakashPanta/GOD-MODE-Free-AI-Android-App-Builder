package com.notes.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9DB4FF),
    secondary = Color(0xFFC6D2FF),
    tertiary = Color(0xFFFFC77D),
    background = Color(0xFF090B10),
    surface = Color(0xFF121722),
    surfaceVariant = Color(0xFF1B2230),
    onPrimary = Color(0xFF071025),
    onBackground = Color(0xFFF3F6FF),
    onSurface = Color(0xFFF3F6FF),
    onSurfaceVariant = Color(0xFFC7CDDB)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF3657D8),
    secondary = Color(0xFF5C6FAE),
    tertiary = Color(0xFF9B5C00),
    background = Color(0xFFF6F7FC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE9ECF6),
    onPrimary = Color.White,
    onBackground = Color(0xFF10131A),
    onSurface = Color(0xFF10131A),
    onSurfaceVariant = Color(0xFF4D5566)
)

@Composable
fun NotesTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
