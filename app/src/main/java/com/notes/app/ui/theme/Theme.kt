package com.notes.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8EA7FF),
    secondary = Color(0xFFB8C7FF),
    background = Color(0xFF10131A),
    surface = Color(0xFF181C25),
    onPrimary = Color(0xFF071025),
    onBackground = Color(0xFFF3F6FF),
    onSurface = Color(0xFFF3F6FF)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF3657D8),
    secondary = Color(0xFF5266A8),
    background = Color(0xFFF8F9FF),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827)
)

@Composable
fun NotesTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
