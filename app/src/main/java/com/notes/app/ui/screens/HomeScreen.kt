package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel
import com.notes.app.Screen
import com.notes.app.SortMode
import com.notes.app.ViewMode

@Composable
fun HomeScreen(vm: NotesViewModel) {
    val notes = vm.filteredNotes
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { HeroDashboard(vm) }
        item {
            OutlinedTextField(
                value = vm.query,
                onValueChange = vm::updateQuery,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if (vm.query.isNotBlank()) IconButton(onClick = { vm.clearQuery() }) { Icon(Icons.Default.Clear, contentDescription = "Clear") } },
                label = { Text("Search notes, labels, tags") },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )
        }
        item { LabelFilters(vm) }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SortMode.values().toList()) { mode ->
                    FilterChip(selected = vm.sortMode == mode, onClick = { vm.setSort(mode) }, label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) })
                }
            }
        }
        if (vm.pinnedNotes.isNotEmpty() && vm.selectedLabel == "All" && vm.query.isBlank()) {
            item { Text("Pinned", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            items(vm.pinnedNotes, key = { it.id }) { note -> NoteCard(vm, note) }
        }
        item { Text(if (vm.query.isBlank()) "Recent Notes" else "Search Results", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        if (notes.isEmpty()) {
            item { SearchAwareEmptyState(query = vm.query, onCreate = { vm.newNote() }) }
        } else if (vm.viewMode == ViewMode.GRID) {
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.heightIn(min = 300.dp, max = 900.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) { items(notes, key = { it.id }) { note -> NoteCard(vm, note) } }
            }
        } else {
            items(notes, key = { it.id }) { note -> if (vm.viewMode == ViewMode.COMPACT) CompactNoteCard(vm, note) else NoteCard(vm, note) }
        }
    }
}

@Composable
fun NotesListScreen(vm: NotesViewModel, title: String) {
    val notes = vm.filteredNotes
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                if (title == "Trash") TextButton(onClick = { vm.emptyTrash() }) { Text("Empty") }
            }
        }
        item { LabelFilters(vm) }
        if (notes.isEmpty()) item { ScreenEmptyState(title) } else items(notes, key = { it.id }) { note -> if (vm.viewMode == ViewMode.COMPACT) CompactNoteCard(vm, note) else NoteCard(vm, note) }
    }
}

@Composable
fun LabelsScreen(vm: NotesViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 120.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item { Text("Labels", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        val labels = vm.labels.filter { it != "All" }
        if (labels.isEmpty()) {
            item { ProfessionalEmptyState(title = "No labels yet", body = "Create labels to organize your notes.", actionText = "Create Note", onAction = { vm.newNote() }) }
        } else {
            items(labels) { label ->
                ElevatedCard(onClick = { vm.selectLabel(label); vm.navigate(Screen.HOME) }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(label, fontWeight = FontWeight.Bold)
                        Text("${vm.activeNotes.count { it.label == label }} notes")
                    }
                }
            }
        }
    }
}
