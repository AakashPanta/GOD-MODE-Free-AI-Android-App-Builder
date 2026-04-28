package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel
import com.notes.app.Screen

@Composable
fun SettingsScreen(vm: NotesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Card(shape = RoundedCornerShape(24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Dark Theme", fontWeight = FontWeight.Bold)
                    Text("Switch between premium dark and light modes.")
                }
                Switch(checked = vm.darkTheme, onCheckedChange = { vm.toggleTheme() })
            }
        }

        Card(shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Offline-first", fontWeight = FontWeight.Bold)
                Text("Notes are saved permanently using local JSON storage.")
            }
        }

        Card(shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Production Structure", fontWeight = FontWeight.Bold)
                Text("Model, storage, ViewModel, screens, and theme are split into separate files.")
            }
        }

        Button(onClick = { vm.navigate(Screen.HOME) }, modifier = Modifier.fillMaxWidth()) {
            Text("Back Home")
        }
    }
}
