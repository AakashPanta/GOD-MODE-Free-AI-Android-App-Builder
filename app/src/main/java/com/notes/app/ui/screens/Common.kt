package com.notes.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notes.app.Note
import com.notes.app.NotesViewModel

@Composable
fun HeroDashboard(vm: NotesViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Your thinking space",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Capture ideas, organize folders, pin priorities, and keep every note offline.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatPill("Notes", vm.activeNotes.size.toString(), Modifier.weight(1f))
                StatPill("Pinned", vm.pinnedNotes.size.toString(), Modifier.weight(1f))
                StatPill("Stars", vm.activeNotes.count { it.favorite }.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatPill(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun FolderFilters(vm: NotesViewModel) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(vm.folders) { folder ->
            FilterChip(
                selected = vm.selectedFolder == folder,
                onClick = { vm.selectFolder(folder) },
                label = { Text(folder) },
                leadingIcon = {
                    if (vm.selectedFolder == folder) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            )
        }
    }
}

@Composable
fun NotesListContent(vm: NotesViewModel, list: List<Note>) {
    if (list.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            val sorted = list.sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.updatedAt })
            items(sorted, key = { it.id }) { note ->
                NoteCard(vm, note)
            }
        }
    }
}

@Composable
fun NoteCard(vm: NotesViewModel, note: Note) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        onClick = { vm.edit(note) },
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (note.pinned) {
                            Icon(
                                Icons.Default.PushPin,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(6.dp))
                        }

                        Text(
                            note.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        note.updatedAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                note.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(note.folder) }, leadingIcon = {
                    Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                })
                AssistChip(onClick = {}, label = { Text("#${note.tag}") })
            }

            Divider(color = MaterialTheme.colorScheme.surfaceVariant)

            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = { vm.togglePin(note) }) {
                    Icon(Icons.Default.PushPin, contentDescription = "Pin")
                }
                IconButton(onClick = { vm.toggleFavorite(note) }) {
                    Icon(if (note.favorite) Icons.Default.Star else Icons.Default.StarBorder, contentDescription = "Favorite")
                }
                IconButton(onClick = { vm.duplicate(note) }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate")
                }

                Spacer(Modifier.weight(1f))

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NoteAlt, contentDescription = null, modifier = Modifier.size(46.dp))
            }
            Text("Nothing here yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Create, restore, or search notes to see them here.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
