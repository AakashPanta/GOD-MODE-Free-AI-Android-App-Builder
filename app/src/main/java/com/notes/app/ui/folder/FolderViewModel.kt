package com.notes.app.ui.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.app.model.Folder
import com.notes.app.repository.NoteRepository
import kotlinx.coroutines.launch

class FolderViewModel(private val repository: NoteRepository) : ViewModel()[11D[K
ViewModel() {

    fun addFolder(folder: Folder) {
        viewModelScope.launch {
            repository.addFolder(folder)
        }
    }

    fun updateFolder(folder: Folder) {
        viewModelScope.launch {
            repository.updateFolder(folder)
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            repository.deleteFolder(folderId)
        }
    }

    fun getFolders() = repository.getFolders()

    fun moveNoteToFolder(noteId: Long, folderId: Long) {
        viewModelScope.launch {
            repository.moveNoteToFolder(noteId, folderId)
        }
    }
}
