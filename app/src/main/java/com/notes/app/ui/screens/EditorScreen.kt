package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    var favorite by remember(editing?.id) { mutableStateOf(editing?.favorite ?: false) }
    var pinned by remember(editing?.id) { mutableStateOf(editing?.pinned ?: false) }
    var preview by remember { mutableStateOf(false) }
    val hasUnsavedChanges = editing == null || title != editing.title || body != editing.body || label != editing.label || tags != editing.tags || color != editing.color || locked != editing.locked || favorite != editing.favorite || pinned != editing.pinned
    fun insert(token: String) { body = if (body.isBlank()) token else body + "
" + token }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp).padding(bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(if (editing == null) "New Note" else "Edit Note", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(if (hasUnsavedChanges) "Unsaved changes" else "Saved", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                IconButton(onClick = { vm.cancelEdit() }) { Icon(Icons.Default.Close, "Cancel") }
                FilledIconButton(onClick = { vm.save(title, body, label, tags, color, locked, favorite, pinned) }) { Icon(Icons.Default.Check, "Save") }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !preview, onClick = { preview = false }, label = { Text("Write") })
            FilterChip(selected = preview, onClick = { preview = true }, label = { Text("Preview") })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Bold", "Italic", "Heading", "Quote").forEach { item -> AssistChip(onClick = { when (item) { "Bold" -> insert("**bold text**"); "Italic" -> insert("_italic text_"); "Heading" -> insert("# Heading"); "Quote" -> insert("> Quote") } }, label = { Text(item) }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Bullet", "Number", "Link", "Checklist").forEach { item -> AssistChip(onClick = { when (item) { "Bullet" -> insert("- Bullet item"); "Number" -> insert("1. Numbered item"); "Link" -> insert("[Link](https://example.com)"); "Checklist" -> insert("- [ ] Checklist item") } }, label = { Text(item) }) }
        }
        OutlinedTextField(value = title, onValueChange = { title = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Title") }, singleLine = true, shape = MaterialTheme.shapes.extraLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = label, onValueChange = { label = it }, modifier = Modifier.weight(1f), label = { Text("Label") }, singleLine = true, shape = MaterialTheme.shapes.extraLarge)
            OutlinedTextField(value = tags, onValueChange = { tags = it }, modifier = Modifier.weight(1f), label = { Text("Tags") }, singleLine = true, shape = MaterialTheme.shapes.extraLarge)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(0xFF8EA7FF, 0xFF7BE4B2, 0xFFFFB26B, 0xFFFF9BCB, 0xFFB69DFF, 0xFFFF6B6B, 0xFF4ECDC4).forEach { c -> FilterChip(selected = color == c, onClick = { color = c }, label = { Text(" ") }, leadingIcon = { Surface(color = Color(c), modifier = Modifier.size(16.dp), shape = MaterialTheme.shapes.small) {} }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = favorite, onClick = { favorite = !favorite }, label = { Text("Favorite") })
            FilterChip(selected = pinned, onClick = { pinned = !pinned }, label = { Text("Pinned") })
            FilterChip(selected = locked, onClick = { locked = !locked }, label = { Text("Locked") })
        }
        if (preview) MarkdownPreview(body) else OutlinedTextField(value = body, onValueChange = { body = it }, modifier = Modifier.fillMaxWidth().heightIn(min = 320.dp), label = { Text("Write your note") }, shape = MaterialTheme.shapes.extraLarge, textStyle = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun MarkdownPreview(content: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (content.isBlank()) Text("Preview is empty", color = MaterialTheme.colorScheme.onSurfaceVariant) else content.lines().forEach { line ->
                when {
                    line.startsWith("# ") -> Text(line.removePrefix("# "), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    line.startsWith("> ") -> Text(line.removePrefix("> "), color = MaterialTheme.colorScheme.primary)
                    line.startsWith("- [ ]") -> Text("☐ " + line.removePrefix("- [ ]").trim())
                    line.startsWith("- ") -> Text("• " + line.removePrefix("- "))
                    line.matches(Regex("^\d+\. .*")) -> Text(line)
                    else -> Text(line.replace("**", "").replace("_", "").replace("==", ""))
                }
            }
        }
    }
}
