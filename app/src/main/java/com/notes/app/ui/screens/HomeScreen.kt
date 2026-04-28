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
import com.notes.app.SortMode

@Composable
fun HomeScreen(vm: NotesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        HeroDashboard(vm)

        OutlinedTextField(
            value = vm.query,
            onValueChange = vm::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            label = { Text("Search notes, labels, tags") },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge
        )

        LabelFilters(vm)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = vm.sortMode == SortMode.UPDATED, onClick = { vm.setSort(SortMode.UPDATED) }, label = { Text("Updated") })
            FilterChip(selected = vm.sortMode == SortMode.TITLE, onClick = { vm.setSort(SortMode.TITLE) }, label = { Text("Title") })
            FilterChip(selected = vm.sortMode == SortMode.LABEL, onClick = { vm.setSort(SortMode.LABEL) }, label = { Text("Label") })
        }

        Text("Notes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        NotesListContent(vm, vm.filteredNotes)
    }
}

@Composable
fun NotesListScreen(vm: NotesViewModel, title: String) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        LabelFilters(vm)
        NotesListContent(vm, vm.filteredNotes)
    }
}

@Composable
fun LabelsScreen(vm: NotesViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Labels", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        LabelFilters(vm)
        vm.labels.filter { it != "All" }.forEach { label ->
            ElevatedCard(onClick = { vm.selectLabel(label); vm.navigate(com.notes.app.Screen.HOME) }) {
                Row(modifier = Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, fontWeight = FontWeight.Bold)
                    Text("${vm.activeNotes.count { it.label == label }} notes")
                }
            }
        }
    }
}
