package com.notes.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.notes.app.NotesViewModel
import com.notes.app.Screen
import com.notes.app.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(vm: NotesViewModel) {
    when (vm.screen) {
        Screen.LOCK -> {
            LockScreen(vm)
            return
        }
        Screen.ONBOARDING -> {
            OnboardingScreen(vm)
            return
        }
        else -> Unit
    }

    DeleteConfirmationDialog(vm)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Notes V3", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${vm.activeNotes.size} notes • ${vm.themeName} • ${vm.viewMode.name.lowercase()}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    if (vm.screen == Screen.EDITOR) {
                        IconButton(onClick = { vm.cancelEdit() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { vm.toggleViewMode() }) {
                        Icon(if (vm.viewMode.name == "GRID") Icons.Default.ViewList else Icons.Default.GridView, contentDescription = "View")
                    }
                    IconButton(onClick = { vm.toggleTheme() }) {
                        Icon(if (vm.darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Theme")
                    }
                    IconButton(onClick = { vm.navigate(Screen.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            if (vm.screen != Screen.EDITOR) {
                ExtendedFloatingActionButton(
                    onClick = { vm.newNote() },
                    icon = { Icon(Icons.Default.Add, contentDescription = "New note") },
                    text = { Text("New Note") }
                )
            }
        },
        bottomBar = {
            if (vm.screen != Screen.EDITOR) {
                NavigationBar {
                    NavigationBarItem(selected = vm.screen == Screen.HOME, onClick = { vm.navigate(Screen.HOME) }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                    NavigationBarItem(selected = vm.screen == Screen.LABELS, onClick = { vm.navigate(Screen.LABELS) }, icon = { Icon(Icons.Default.Label, null) }, label = { Text("Labels") })
                    NavigationBarItem(selected = vm.screen == Screen.FAVORITES, onClick = { vm.navigate(Screen.FAVORITES) }, icon = { Icon(Icons.Default.Star, null) }, label = { Text("Stars") })
                    NavigationBarItem(selected = vm.screen == Screen.SETTINGS, onClick = { vm.navigate(Screen.SETTINGS) }, icon = { Icon(Icons.Default.Tune, null) }, label = { Text("More") })
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (vm.screen) {
                Screen.HOME -> HomeScreen(vm)
                Screen.EDITOR -> EditorScreen(vm)
                Screen.LABELS -> LabelsScreen(vm)
                Screen.FAVORITES -> NotesListScreen(vm, "Favorites")
                Screen.ARCHIVE -> NotesListScreen(vm, "Archive")
                Screen.TRASH -> NotesListScreen(vm, "Trash")
                Screen.SETTINGS -> SettingsScreen(vm)
                Screen.EXPORT_IMPORT -> ExportImportScreen(vm)
                Screen.ABOUT -> AboutScreen(vm)
                else -> HomeScreen(vm)
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(vm: NotesViewModel) {
    val note = vm.showDeleteDialog ?: return
    AlertDialog(
        onDismissRequest = { vm.dismissDeleteDialog() },
        title = { Text("Move to Trash?") },
        text = { Text("“${note.title}” will be moved to Trash. You can restore it later.") },
        confirmButton = { Button(onClick = { vm.moveToTrashConfirmed() }) { Text("Move") } },
        dismissButton = { TextButton(onClick = { vm.dismissDeleteDialog() }) { Text("Cancel") } }
    )
}
