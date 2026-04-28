package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notes.app.Note
import com.notes.app.NotesViewModel
import com.notes.app.ViewMode

@Composable
fun HeroDashboard(vm: NotesViewModel) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(32.dp)) {
        Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("NotesMaster-inspired V3", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Labels, search, themes, export/import, grid/list view, lock mode, and formatting helpers.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatPill("Notes", vm.activeNotes.size.toString(), Modifier.weight(1f))
                StatPill("Labels", (vm.labels.size - 1).coerceAtLeast(0).toString(), Modifier.weight(1f))
                StatPill("Stars", vm.activeNotes.count { it.favorite }.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun LabelFilters(vm: NotesViewModel) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vm.labels) { label ->
            FilterChip(
                selected = vm.selectedLabel == label,
                onClick = { vm.selectLabel(label) },
                label = { Text(label) },
                leadingIcon = { if (vm.selectedLabel == label) Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
            )
        }
    }
}

@Composable
fun NotesListContent(vm: NotesViewModel, list: List<Note>) {
    if (list.isEmpty()) {
        EmptyState()
        return
    }

    if (vm.viewMode == ViewMode.GRID) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list, key = { it.id }) { note ->
                NoteCard(vm, note)
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 96.dp)) {
            items(list, key = { it.id }) { note ->
                NoteCard(vm, note)
            }
        }
    }
}

@Composable
fun NoteCard(vm: NotesViewModel, note: Note) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), onClick = { vm.edit(note) }) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (note.pinned) Icon(Icons.Default.PushPin, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                        if (note.locked) Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.width(6.dp))
                        Text(note.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Text(note.updatedAt, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Surface(shape = RoundedCornerShape(50), color = Color(note.color).copy(alpha = 0.35f), modifier = Modifier.size(18.dp)) {}
            }

            Text(note.body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 4, overflow = TextOverflow.Ellipsis)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(note.label) }, leadingIcon = { Icon(Icons.Default.Label, null, modifier = Modifier.size(16.dp)) })
                AssistChip(onClick = {}, label = { Text(note.tags) })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = { vm.togglePin(note) }) { Icon(Icons.Default.PushPin, "Pin") }
                IconButton(onClick = { vm.toggleFavorite(note) }) { Icon(if (note.favorite) Icons.Default.Star else Icons.Default.StarBorder, "Favorite") }
                IconButton(onClick = { vm.duplicate(note) }) { Icon(Icons.Default.ContentCopy, "Duplicate") }
                Spacer(Modifier.weight(1f))
                if (note.trashed || note.archived) {
                    IconButton(onClick = { vm.restore(note) }) { Icon(Icons.Default.Restore, "Restore") }
                } else {
                    IconButton(onClick = { vm.archive(note) }) { Icon(Icons.Default.Archive, "Archive") }
                    IconButton(onClick = { vm.requestDelete(note) }) { Icon(Icons.Default.Delete, "Trash") }
                }
                if (note.trashed) IconButton(onClick = { vm.deleteForever(note) }) { Icon(Icons.Default.DeleteForever, "Delete forever") }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(30.dp)) {
            Icon(Icons.Default.NoteAlt, null, modifier = Modifier.size(56.dp))
            Text("Nothing here yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Create, restore, label, or search notes to see them here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
