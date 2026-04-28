package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel

@Composable
fun LockScreen(vm: NotesViewModel) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        ElevatedCard(shape = RoundedCornerShape(32.dp)) {
            Column(modifier = Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(64.dp))
                Text("Notes Locked", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = pin, onValueChange = { pin = it; error = false }, label = { Text("PIN") }, isError = error, singleLine = true)
                Button(onClick = { error = !vm.unlock(pin) }, modifier = Modifier.fillMaxWidth()) { Text("Unlock") }
                if (error) Text("Wrong PIN", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun OnboardingScreen(vm: NotesViewModel) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        ElevatedCard(shape = RoundedCornerShape(36.dp)) {
            Column(modifier = Modifier.padding(30.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Icon(Icons.Default.NoteAlt, null, modifier = Modifier.size(72.dp))
                Text("Notes", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Create, edit, organize, favorite, search, and save notes locally.")
                Button(onClick = { vm.finishOnboarding() }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = MaterialTheme.shapes.extraLarge) { Text("Start Writing") }
            }
        }
    }
}
