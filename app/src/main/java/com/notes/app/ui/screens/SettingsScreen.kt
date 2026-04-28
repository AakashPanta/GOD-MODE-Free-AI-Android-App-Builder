package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun SettingsScreen(vm: NotesViewModel) {
    var pin by remember { mutableStateOf("1234") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        SettingsCard(Icons.Default.Style, "Appearance", "Theme: ${vm.themeName}") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Indigo", "Emerald", "Sunset", "Rose").forEach { theme ->
                    AssistChip(onClick = { vm.setTheme(theme) }, label = { Text(theme) })
                }
            }
        }

        SettingsCard(Icons.Default.DarkMode, "Dark Mode", "Switch light and dark mode.") {
            Switch(checked = vm.darkTheme, onCheckedChange = { vm.toggleTheme() })
        }

        SettingsCard(Icons.Default.GridView, "View Options", "Toggle between list and grid browsing.") {
            Button(onClick = { vm.toggleViewMode() }) { Text(vm.viewMode.name) }
        }

        SettingsCard(Icons.Default.Lock, "App Lock", "Enable PIN lock. Default PIN is 1234.") {
            OutlinedTextField(value = pin, onValueChange = { pin = it }, label = { Text("PIN") }, singleLine = true)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.setLock(true, pin) }) { Text("Enable") }
                OutlinedButton(onClick = { vm.setLock(false, pin) }) { Text("Disable") }
            }
        }

        SettingsCard(Icons.Default.ImportExport, "Export / Import", "Backup or restore from local app storage.") {
            Button(onClick = { vm.navigate(Screen.EXPORT_IMPORT) }) { Text("Open") }
        }

        SettingsCard(Icons.Default.Info, "About Notes V3", "Inspired by NotesMaster features, rebuilt natively in Compose.") {
            Button(onClick = { vm.navigate(Screen.ABOUT) }) { Text("About") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { vm.navigate(Screen.ARCHIVE) }, modifier = Modifier.weight(1f)) { Text("Archive") }
            Button(onClick = { vm.navigate(Screen.TRASH) }, modifier = Modifier.weight(1f)) { Text("Trash") }
        }

        Button(onClick = { vm.navigate(Screen.HOME) }, modifier = Modifier.fillMaxWidth()) {
            Text("Back Home")
        }
    }
}

@Composable
fun SettingsCard(icon: ImageVector, title: String, body: String, content: @Composable ColumnScope.() -> Unit = {}) {
    Card(shape = RoundedCornerShape(28.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, null)
                Column {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            content()
        }
    }
}

@Composable
fun ExportImportScreen(vm: NotesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Export / Import", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("This V3 build creates an export JSON inside private app storage and can import it back.")
        Button(onClick = { vm.exportNotes() }, modifier = Modifier.fillMaxWidth()) { Text("Export Notes") }
        Button(onClick = { vm.importLastExport() }, modifier = Modifier.fillMaxWidth()) { Text("Import Last Export") }
        if (vm.exportMessage.isNotBlank()) {
            Text(vm.exportMessage, color = MaterialTheme.colorScheme.primary)
        }
        Button(onClick = { vm.navigate(Screen.SETTINGS) }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
fun AboutScreen(vm: NotesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("About Notes V3", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Notes V3 combines your existing native Compose app with feature ideas from NotesMaster 3.0.4: labels, search, material themes, export/import, view modes, formatting helpers, lock mode, and refined settings.")
        Text("Package: com.notes.app")
        Button(onClick = { vm.navigate(Screen.SETTINGS) }, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}
