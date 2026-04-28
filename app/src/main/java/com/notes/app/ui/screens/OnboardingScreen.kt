package com.notes.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notes.app.NotesViewModel

@Composable
fun OnboardingScreen(vm: NotesViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(shape = RoundedCornerShape(32.dp)) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Icon(Icons.Default.NoteAlt, contentDescription = null, modifier = Modifier.size(72.dp))

                Text("Notes", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

                Text(
                    "A fast offline notes app with folders, pinned notes, favorites, archive, trash, theme support, and persistent storage.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(
                    onClick = { vm.finishOnboarding() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Writing")
                }
            }
        }
    }
}
