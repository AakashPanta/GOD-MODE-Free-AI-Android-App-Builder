package com.notes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notes.app.ui.NotesApp
import com.notes.app.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: NotesViewModel = viewModel()
            NotesTheme(darkTheme = vm.darkTheme) {
                NotesApp(vm = vm)
            }
        }
    }
}
