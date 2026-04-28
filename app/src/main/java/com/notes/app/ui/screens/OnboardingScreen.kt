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
        ElevatedCard(shape = RoundedCornerShape(36.dp)) {
            Column(
                modifier = Modifier.padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Default.NoteAlt,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(20.dp)
                            .size(64.dp)
                    )
                }

                Text("Notes V2", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)

                Text(
                    "A polished offline notes workspace with folders, favorites, pinned notes, archive, trash, premium themes, and persistent storage.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Button(
                    onClick = { vm.finishOnboarding() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("Start Writing")
                }
            }
        }
    }
}
