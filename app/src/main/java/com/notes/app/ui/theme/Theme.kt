package com.notes.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun darkScheme(themeName: String) = when (themeName) {
    "Emerald" -> darkColorScheme(primary = Color(0xFF7BE4B2), secondary = Color(0xFFA8F0CF), background = Color(0xFF06110D), surface = Color(0xFF101C17))
    "Sunset" -> darkColorScheme(primary = Color(0xFFFFB26B), secondary = Color(0xFFFFD0A3), background = Color(0xFF120B08), surface = Color(0xFF211713))
    "Rose" -> darkColorScheme(primary = Color(0xFFFF9BCB), secondary = Color(0xFFFFC1DC), background = Color(0xFF14070F), surface = Color(0xFF22101A))
    else -> darkColorScheme(primary = Color(0xFF9DB4FF), secondary = Color(0xFFC6D2FF), background = Color(0xFF090B10), surface = Color(0xFF121722))
}

private fun lightScheme(themeName: String) = when (themeName) {
    "Emerald" -> lightColorScheme(primary = Color(0xFF007A4D), secondary = Color(0xFF276B50), background = Color(0xFFF3FCF7), surface = Color.White)
    "Sunset" -> lightColorScheme(primary = Color(0xFFB75B00), secondary = Color(0xFF8F5C2D), background = Color(0xFFFFF7F0), surface = Color.White)
    "Rose" -> lightColorScheme(primary = Color(0xFFC02769), secondary = Color(0xFF8F4B68), background = Color(0xFFFFF5FA), surface = Color.White)
    else -> lightColorScheme(primary = Color(0xFF3657D8), secondary = Color(0xFF5C6FAE), background = Color(0xFFF6F7FC), surface = Color.White)
}

@Composable
fun NotesTheme(themeName: String, darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkScheme(themeName) else lightScheme(themeName),
        content = content
    )
}
