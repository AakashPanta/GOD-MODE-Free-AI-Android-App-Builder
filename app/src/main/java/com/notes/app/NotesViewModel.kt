package com.notes.app

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.util.UUID

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val store = NotesStore(application)

    var screen by mutableStateOf(
        if (store.hasCompletedOnboarding()) Screen.HOME else Screen.ONBOARDING
    )
        private set

    var selectedNote by mutableStateOf<Note?>(null)
        private set

    var query by mutableStateOf("")
        private set

    var selectedFolder by mutableStateOf("All")
        private set

    var darkTheme by mutableStateOf(store.isDarkTheme())
        private set

    var showDeleteDialog by mutableStateOf<Note?>(null)
        private set

    var notes by mutableStateOf(loadInitialNotes())
        private set

    private fun loadInitialNotes(): List<Note> {
        val saved = store.loadNotes()
        return saved.ifEmpty {
            listOf(
                Note(
                    title = "Welcome to Notes",
                    body = "Your notes are now saved permanently on this device.",
                    folder = "Inbox",
                    tag = "Start",
                    pinned = true
                ),
                Note(
                    title = "Production upgrade installed",
                    body = "This version includes persistence, editor polish, folders, filters, favorites, theme toggle, onboarding, and production structure.",
                    folder = "Productivity",
                    tag = "Upgrade",
                    favorite = true
                )
            )
        }
    }

    private fun persist() {
        store.saveNotes(notes)
    }

    val folders: List<String>
        get() = listOf("All") + notes
            .filter { !it.trashed && !it.archived }
            .map { it.folder.ifBlank { "Personal" } }
            .distinct()
            .sorted()

    val activeNotes: List<Note>
        get() = notes.filter { !it.trashed && !it.archived }

    val filteredNotes: List<Note>
        get() {
            val base = when (screen) {
                Screen.TRASH -> notes.filter { it.trashed }
                Screen.ARCHIVE -> notes.filter { it.archived && !it.trashed }
                Screen.FAVORITES -> activeNotes.filter { it.favorite }
                else -> activeNotes
            }.let { list ->
                if (selectedFolder == "All") list else list.filter { it.folder == selectedFolder }
            }

            if (query.isBlank()) return base

            return base.filter {
                it.title.contains(query, true) ||
                    it.body.contains(query, true) ||
                    it.folder.contains(query, true) ||
                    it.tag.contains(query, true)
            }
        }

    val pinnedNotes: List<Note>
        get() = activeNotes.filter { it.pinned }

    fun finishOnboarding() {
        store.setOnboardingDone()
        screen = Screen.HOME
    }

    fun navigate(target: Screen) {
        screen = target
        if (target != Screen.EDITOR) selectedNote = null
    }

    fun updateQuery(value: String) {
        query = value
    }

    fun selectFolder(value: String) {
        selectedFolder = value
    }

    fun toggleTheme() {
        darkTheme = !darkTheme
        store.setDarkTheme(darkTheme)
    }

    fun newNote() {
        selectedNote = null
        screen = Screen.EDITOR
    }

    fun edit(note: Note) {
        selectedNote = note
        screen = Screen.EDITOR
    }

    fun cancelEdit() {
        selectedNote = null
        screen = Screen.HOME
    }

    fun save(title: String, body: String, folder: String, tag: String) {
        val cleanTitle = title.ifBlank { "Untitled Note" }
        val cleanBody = body.ifBlank { "No content" }
        val cleanFolder = folder.ifBlank { "Personal" }
        val cleanTag = tag.ifBlank { "General" }
        val existing = selectedNote

        notes = if (existing == null) {
            listOf(
                Note(
                    title = cleanTitle,
                    body = cleanBody,
                    folder = cleanFolder,
                    tag = cleanTag
                )
            ) + notes
        } else {
            notes.map {
                if (it.id == existing.id) {
                    it.copy(
                        title = cleanTitle,
                        body = cleanBody,
                        folder = cleanFolder,
                        tag = cleanTag,
                        updatedAt = now()
                    )
                } else {
                    it
                }
            }
        }

        selectedNote = null
        screen = Screen.HOME
        persist()
    }

    fun togglePin(note: Note) {
        notes = notes.map {
            if (it.id == note.id) it.copy(pinned = !it.pinned, updatedAt = now()) else it
        }
        persist()
    }

    fun toggleFavorite(note: Note) {
        notes = notes.map {
            if (it.id == note.id) it.copy(favorite = !it.favorite, updatedAt = now()) else it
        }
        persist()
    }

    fun duplicate(note: Note) {
        notes = listOf(
            note.copy(
                id = UUID.randomUUID().toString(),
                title = note.title + " Copy",
                updatedAt = now()
            )
        ) + notes
        persist()
    }

    fun archive(note: Note) {
        notes = notes.map {
            if (it.id == note.id) it.copy(archived = true, trashed = false, updatedAt = now()) else it
        }
        persist()
    }

    fun requestDelete(note: Note) {
        showDeleteDialog = note
    }

    fun dismissDeleteDialog() {
        showDeleteDialog = null
    }

    fun moveToTrashConfirmed() {
        val note = showDeleteDialog ?: return
        notes = notes.map {
            if (it.id == note.id) it.copy(trashed = true, archived = false, updatedAt = now()) else it
        }
        showDeleteDialog = null
        persist()
    }

    fun restore(note: Note) {
        notes = notes.map {
            if (it.id == note.id) it.copy(trashed = false, archived = false, updatedAt = now()) else it
        }
        persist()
    }

    fun deleteForever(note: Note) {
        notes = notes.filterNot { it.id == note.id }
        persist()
    }
}
