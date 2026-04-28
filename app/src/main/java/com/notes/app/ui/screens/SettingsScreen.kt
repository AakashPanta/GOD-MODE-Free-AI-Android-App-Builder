package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel
import com.notes.app.Screen
import com.notes.app.ViewMode

@Composable
fun SettingsScreen(vm: NotesViewModel) {
    var pin by remember { mutableStateOf("1234") }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp).padding(bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        SettingsCard(Icons.Default.Style, "Appearance", "Dark mode, accent theme, and default view.") {
            Switch(checked = vm.darkTheme, onCheckedChange = { vm.toggleTheme() })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Indigo", "Emerald", "Sunset", "Rose").forEach { theme -> AssistChip(onClick = { vm.setTheme(theme) }, label = { Text(theme) }) } }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { ViewMode.values().forEach { mode -> AssistChip(onClick = { vm.setViewMode(mode) }, label = { Text(mode.name) }) } }
        }
        SettingsCard(Icons.Default.Lock, "Security", "Enable a basic app PIN lock.") {
            OutlinedTextField(value = pin, onValueChange = { pin = it }, label = { Text("PIN") }, singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Button(onClick = { vm.setLock(true, pin) }) { Text("Enable") }; OutlinedButton(onClick = { vm.setLock(false, pin) }) { Text("Disable") } }
        }
        SettingsCard(Icons.Default.ImportExport, "Backup & Export", "Export or import notes as local JSON.") { Button(onClick = { vm.navigate(Screen.EXPORT_IMPORT) }) { Text("Open Backup") } }
        SettingsCard(Icons.Default.Info, "About", "Notes V4 • Local-first notes app.") { Button(onClick = { vm.navigate(Screen.ABOUT) }) { Text("About") } }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Button(onClick = { vm.navigate(Screen.ARCHIVE) }, modifier = Modifier.weight(1f)) { Text("Archive") }; Button(onClick = { vm.navigate(Screen.TRASH) }, modifier = Modifier.weight(1f)) { Text("Trash") } }
        Button(onClick = { vm.navigate(Screen.HOME) }, modifier = Modifier.fillMaxWidth()) { Text("Back Home") }
    }
}

@Composable
fun SettingsCard(icon: ImageVector, title: String, body: String, content: @Composable ColumnScope.() -> Unit = {}) {
    Card(shape = RoundedCornerShape(28.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { Icon(icon, null); Column { Text(title, fontWeight = FontWeight.Bold); Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            content()
        }
    }
}

@Composable
fun ExportImportScreen(vm: NotesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Backup & Export", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Export all notes as local JSON or import the latest local export.")
        Button(onClick = { vm.exportNotes() }, modifier = Modifier.fillMaxWidth()) { Text("Export Notes") }
        Button(onClick = { vm.importLastExport() }, modifier = Modifier.fillMaxWidth()) { Text("Import Last Export") }
        if (vm.exportMessage.isNotBlank()) Text(vm.exportMessage, color = MaterialTheme.colorScheme.primary)
        Button(onClick = { vm.navigate(Screen.SETTINGS) }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun AboutScreen(vm: NotesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("About Notes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Notes V4")
        Text("Local-first notes app")
        Text("Version 4.0.0")
        Text("Create notes, edit notes, search, sort, filter, favorite, label, pin, archive, and export.")
        Button(onClick = { vm.navigate(Screen.SETTINGS) }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun InsightsScreen(vm: NotesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Insights", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { StatPill("Active", vm.activeNotes.size.toString(), Modifier.weight(1f)); StatPill("Words", vm.totalWords.toString(), Modifier.weight(1f)); StatPill("Labels", (vm.labels.size - 1).coerceAtLeast(0).toString(), Modifier.weight(1f)) }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { StatPill("Pinned", vm.pinnedNotes.size.toString(), Modifier.weight(1f)); StatPill("Stars", vm.favoriteNotes.size.toString(), Modifier.weight(1f)); StatPill("Trash", vm.trashNotes.size.toString(), Modifier.weight(1f)) }
        Button(onClick = { vm.navigate(Screen.HOME) }, modifier = Modifier.fillMaxWidth()) { Text("Back Home") }
    }
}
