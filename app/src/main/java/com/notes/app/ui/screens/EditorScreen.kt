package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel

@Composable
fun EditorScreen(vm: NotesViewModel) {
    val editing = vm.selectedNote

    var title by remember(editing?.id) { mutableStateOf(editing?.title ?: "") }
    var body by remember(editing?.id) { mutableStateOf(editing?.body ?: "") }
    var folder by remember(editing?.id) { mutableStateOf(editing?.folder ?: "Personal") }
    var tag by remember(editing?.id) { mutableStateOf(editing?.tag ?: "General") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    if (editing == null) "New Note" else "Edit Note",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Autosaves when you press Save",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = { vm.cancelEdit() }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                }
                FilledIconButton(onClick = { vm.save(title, body, folder, tag) }) {
                    Icon(Icons.Default.Check, contentDescription = "Save")
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Title") },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleLarge,
            shape = MaterialTheme.shapes.extraLarge
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = folder,
                onValueChange = { folder = it },
                modifier = Modifier.weight(1f),
                label = { Text("Folder") },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )

            OutlinedTextField(
                value = tag,
                onValueChange = { tag = it },
                modifier = Modifier.weight(1f),
                label = { Text("Tag") },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )
        }

        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text("Write your note") },
            shape = MaterialTheme.shapes.extraLarge,
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = { vm.save(title, body, folder, tag) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text("Save Note")
        }
    }
}
