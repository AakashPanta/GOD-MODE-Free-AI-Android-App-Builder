package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel

@Composable
fun EditorScreen(vm: NotesViewModel) {
    val editing = vm.selectedNote

    var title by remember(editing?.id) { mutableStateOf(editing?.title ?: "") }
    var body by remember(editing?.id) { mutableStateOf(editing?.body ?: "") }
    var label by remember(editing?.id) { mutableStateOf(editing?.label ?: "Personal") }
    var tags by remember(editing?.id) { mutableStateOf(editing?.tags ?: "General") }
    var color by remember(editing?.id) { mutableStateOf(editing?.color ?: 0xFF8EA7FF) }
    var locked by remember(editing?.id) { mutableStateOf(editing?.locked ?: false) }

    fun insert(token: String) {
        body = if (body.isBlank()) token else body + "\n" + token
    }

    Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(if (editing == null) "New Note" else "Edit Note", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Formatting helpers available", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row {
                IconButton(onClick = { vm.cancelEdit() }) { Icon(Icons.Default.Close, "Cancel") }
                FilledIconButton(onClick = { vm.save(title, body, label, tags, color, locked) }) { Icon(Icons.Default.Check, "Save") }
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Bold", "Italic", "Heading", "Quote", "Bullet", "Number", "Link", "Divider", "Highlight")) { item ->
                AssistChip(
                    onClick = {
                        when (item) {
                            "Bold" -> insert("**bold text**")
                            "Italic" -> insert("_italic text_")
                            "Heading" -> insert("# Heading")
                            "Quote" -> insert("> Quote")
                            "Bullet" -> insert("- Bullet item")
                            "Number" -> insert("1. Numbered item")
                            "Link" -> insert("[title](https://example.com)")
                            "Divider" -> insert("---")
                            "Highlight" -> insert("==highlight==")
                        }
                    },
                    label = { Text(item) }
                )
            }
        }

        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Title") }, singleLine = true, shape = MaterialTheme.shapes.extraLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = label, onValueChange = { label = it }, modifier = Modifier.weight(1f), label = { Text("Label") }, singleLine = true, shape = MaterialTheme.shapes.extraLarge)
            OutlinedTextField(value = tags, onValueChange = { tags = it }, modifier = Modifier.weight(1f), label = { Text("Tags") }, singleLine = true, shape = MaterialTheme.shapes.extraLarge)
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf(0xFF8EA7FF, 0xFF7BE4B2, 0xFFFFB26B, 0xFFFF9BCB, 0xFFB69DFF)) { c ->
                FilterChip(
                    selected = color == c,
                    onClick = { color = c },
                    label = { Text(" ") },
                    leadingIcon = { Surface(color = Color(c), modifier = Modifier.size(16.dp), shape = MaterialTheme.shapes.small) {} }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("Lock this note")
            Switch(checked = locked, onCheckedChange = { locked = it })
        }

        OutlinedTextField(value = body, onValueChange = { body = it }, modifier = Modifier.fillMaxWidth().weight(1f), label = { Text("Write your note") }, shape = MaterialTheme.shapes.extraLarge, textStyle = MaterialTheme.typography.bodyLarge)

        Button(onClick = { vm.save(title, body, label, tags, color, locked) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.extraLarge) {
            Text("Save Note")
        }
    }
}
