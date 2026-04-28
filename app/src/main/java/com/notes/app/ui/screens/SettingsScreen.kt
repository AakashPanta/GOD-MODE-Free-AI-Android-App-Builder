package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel
import com.notes.app.Screen

@Composable
fun SettingsScreen(vm: NotesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        SettingsCard(
            icon = Icons.Default.Style,
            title = "Appearance",
            body = "Switch between premium dark and light modes.",
            trailing = {
                Switch(checked = vm.darkTheme, onCheckedChange = { vm.toggleTheme() })
            }
        )

        SettingsCard(
            icon = Icons.Default.Storage,
            title = "Offline-first",
            body = "Notes are saved permanently using local JSON storage."
        )

        SettingsCard(
            icon = Icons.Default.Info,
            title = "Notes V2",
            body = "Premium UI, production structure, favorites, folders, archive, trash, and signed builds."
        )

        Button(onClick = { vm.navigate(Screen.HOME) }, modifier = Modifier.fillMaxWidth()) {
            Text("Back Home")
        }
    }
}

@Composable
fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Card(shape = RoundedCornerShape(28.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, contentDescription = null)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (trailing != null) trailing()
        }
    }
}
