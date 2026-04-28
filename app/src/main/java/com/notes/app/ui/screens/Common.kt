package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.Note
import com.notes.app.NotesViewModel

@Composable
fun Dashboard(vm: NotesViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Notes", vm.activeNotes.size.toString(), Modifier.weight(1f))
        StatCard("Pinned", vm.pinnedNotes.size.toString(), Modifier.weight(1f))
        StatCard("Stars", vm.activeNotes.count { it.favorite }.toString(), Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(22.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun FolderFilters(vm: NotesViewModel) {
    LazyColumn(
        modifier = Modifier.heightIn(max = 92.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.folders.forEach { folder ->
                    FilterChip(
                        selected = vm.selectedFolder == folder,
                        onClick = { vm.selectFolder(folder) },
                        label = { Text(folder) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotesListContent(vm: NotesViewModel, list: List<Note>) {
    if (list.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val sorted = list.sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.updatedAt })
            items(sorted, key = { it.id }) { note ->
                NoteCard(vm, note)
            }
        }
    }
}

@Composable
fun NoteCard(vm: NotesViewModel, note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        onClick = { vm.edit(note) }
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(note.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(note.updatedAt, style = MaterialTheme.typography.labelSmall)
                }

                if (note.pinned) {
                    Icon(Icons.Default.PushPin, contentDescription = null)
                }
            }

            Text(note.body, style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(note.folder) })
                AssistChip(onClick = {}, label = { Text("#${note.tag}") })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { vm.togglePin(note) }) {
                    Icon(Icons.Default.PushPin, contentDescription = "Pin")
                }
                IconButton(onClick = { vm.toggleFavorite(note) }) {
                    Icon(if (note.favorite) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = "Favorite")
                }
                IconButton(onClick = { vm.duplicate(note) }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate")
                }

                if (note.trashed || note.archived) {
                    IconButton(onClick = { vm.restore(note) }) {
                        Icon(Icons.Default.Restore, contentDescription = "Restore")
                    }
                } else {
                    IconButton(onClick = { vm.archive(note) }) {
                        Icon(Icons.Default.Archive, contentDescription = "Archive")
                    }
                    IconButton(onClick = { vm.requestDelete(note) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Trash")
                    }
                }

                if (note.trashed) {
                    IconButton(onClick = { vm.deleteForever(note) }) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Delete forever")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.NoteAlt, contentDescription = null, modifier = Modifier.size(56.dp))
            Text("No notes here", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Create, restore, or search notes to see them here.")
        }
    }
}
