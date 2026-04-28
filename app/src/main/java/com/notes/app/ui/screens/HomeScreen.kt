package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel

@Composable
fun HomeScreen(vm: NotesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        HeroDashboard(vm)

        OutlinedTextField(
            value = vm.query,
            onValueChange = vm::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search notes, folders, or tags") },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge
        )

        FolderFilters(vm)

        if (vm.pinnedNotes.isNotEmpty()) {
            Text("Pinned", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            NotesListContent(vm, vm.pinnedNotes)
        } else {
            Text("All Notes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            NotesListContent(vm, vm.filteredNotes)
        }
    }
}

@Composable
fun NotesListScreen(vm: NotesViewModel, title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        FolderFilters(vm)
        NotesListContent(vm, vm.filteredNotes)
    }
}
