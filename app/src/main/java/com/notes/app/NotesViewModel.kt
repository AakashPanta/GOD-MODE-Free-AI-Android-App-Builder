package com.notes.app

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.util.UUID

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val store = NotesStore(application)

    var unlocked by mutableStateOf(!store.appLockEnabled())
        private set
    var screen by mutableStateOf(if (store.appLockEnabled()) Screen.LOCK else if (store.hasCompletedOnboarding()) Screen.HOME else Screen.ONBOARDING)
        private set
    var selectedNote by mutableStateOf<Note?>(null)
        private set
    var query by mutableStateOf("")
        private set
    var selectedLabel by mutableStateOf("All")
        private set
    var darkTheme by mutableStateOf(store.isDarkTheme())
        private set
    var themeName by mutableStateOf(store.themeName())
        private set
    var viewMode by mutableStateOf(store.viewMode())
        private set
    var sortMode by mutableStateOf(store.sortMode())
        private set
    var showDeleteDialog by mutableStateOf<Note?>(null)
        private set
    var exportMessage by mutableStateOf("")
        private set
    var snackbarMessage by mutableStateOf("")
        private set
    var notes by mutableStateOf(loadInitialNotes())
        private set

    private fun loadInitialNotes(): List<Note> {
        val saved = store.loadNotes()
        return saved.ifEmpty {
            listOf(
                Note(title = "Welcome to Notes", body = "Capture, organize, and find your notes faster.", label = "Inbox", tags = "Start", pinned = true),
                Note(title = "MVP Ready", body = "Create, edit, delete, favorite, label, search, sort, filter, and persist notes locally.", label = "Productivity", tags = "MVP", favorite = true)
            )
        }
    }

    private fun persist() { store.saveNotes(notes) }

    val labels: List<String>
        get() = listOf("All") + notes.filter { !it.trashed && !it.archived }.map { it.label.ifBlank { "Personal" } }.distinct().sorted()
    val activeNotes: List<Note>
        get() = notes.filter { !it.trashed && !it.archived }
    val pinnedNotes: List<Note>
        get() = activeNotes.filter { it.pinned }
    val favoriteNotes: List<Note>
        get() = activeNotes.filter { it.favorite }
    val archivedNotes: List<Note>
        get() = notes.filter { it.archived && !it.trashed }
    val trashNotes: List<Note>
        get() = notes.filter { it.trashed }
    val totalWords: Int
        get() = activeNotes.sumOf { it.body.split(Regex("\s+")).filter { word -> word.isNotBlank() }.size }

    val filteredNotes: List<Note>
        get() {
            val base = when (screen) {
                Screen.TRASH -> trashNotes
                Screen.ARCHIVE -> archivedNotes
                Screen.FAVORITES -> favoriteNotes
                else -> activeNotes
            }.let { list -> if (selectedLabel == "All") list else list.filter { it.label == selectedLabel } }

            val searched = if (query.isBlank()) base else base.filter {
                it.title.contains(query, true) || it.body.contains(query, true) || it.label.contains(query, true) || it.tags.contains(query, true)
            }

            return when (sortMode) {
                SortMode.TITLE -> searched.sortedBy { it.title.lowercase() }
                SortMode.LABEL -> searched.sortedBy { it.label.lowercase() }
                SortMode.CREATED -> searched.sortedByDescending { it.createdAt }
                SortMode.UPDATED -> searched.sortedByDescending { it.updatedAt }
            }
        }

    fun consumeSnackbar() { snackbarMessage = "" }
    fun unlock(pin: String): Boolean {
        val ok = pin == store.lockPin()
        if (ok) {
            unlocked = true
            screen = if (store.hasCompletedOnboarding()) Screen.HOME else Screen.ONBOARDING
        }
        return ok
    }
    fun finishOnboarding() { store.setOnboardingDone(); screen = Screen.HOME }
    fun navigate(target: Screen) { screen = target; if (target != Screen.EDITOR) selectedNote = null }
    fun updateQuery(value: String) { query = value }
    fun clearQuery() { query = "" }
    fun selectLabel(value: String) { selectedLabel = value }
    fun toggleTheme() { darkTheme = !darkTheme; store.setDarkTheme(darkTheme) }
    fun setTheme(value: String) { themeName = value; store.setThemeName(value) }
    fun setViewMode(value: ViewMode) { viewMode = value; store.setViewMode(value) }
    fun cycleViewMode() {
        viewMode = when (viewMode) { ViewMode.LIST -> ViewMode.GRID; ViewMode.GRID -> ViewMode.COMPACT; ViewMode.COMPACT -> ViewMode.LIST }
        store.setViewMode(viewMode)
    }
    fun setSort(value: SortMode) { sortMode = value; store.setSortMode(value) }
    fun setLock(enabled: Boolean, pin: String) { store.setAppLockEnabled(enabled); if (pin.length >= 4) store.setLockPin(pin); snackbarMessage = if (enabled) "App lock enabled" else "App lock disabled" }
    fun newNote() { selectedNote = null; screen = Screen.EDITOR }
    fun edit(note: Note) { selectedNote = note; screen = Screen.EDITOR }
    fun cancelEdit() { selectedNote = null; screen = Screen.HOME }

    fun save(title: String, body: String, label: String, tags: String, color: Long, locked: Boolean, favorite: Boolean, pinned: Boolean) {
        val cleanTitle = title.ifBlank { "Untitled Note" }
        val cleanBody = body.ifBlank { "No content" }
        val cleanLabel = label.ifBlank { "Personal" }
        val cleanTags = tags.ifBlank { "General" }
        val existing = selectedNote
        notes = if (existing == null || notes.none { it.id == existing.id }) {
            listOf(Note(title = cleanTitle, body = cleanBody, label = cleanLabel, tags = cleanTags, color = color, locked = locked, favorite = favorite, pinned = pinned)) + notes
        } else {
            notes.map { if (it.id == existing.id) it.copy(title = cleanTitle, body = cleanBody, label = cleanLabel, tags = cleanTags, color = color, locked = locked, favorite = favorite, pinned = pinned, updatedAt = now()) else it }
        }
        selectedNote = null
        screen = Screen.HOME
        persist()
        snackbarMessage = "Note saved"
    }

    fun togglePin(note: Note) { notes = notes.map { if (it.id == note.id) it.copy(pinned = !it.pinned, updatedAt = now()) else it }; persist() }
    fun toggleFavorite(note: Note) { notes = notes.map { if (it.id == note.id) it.copy(favorite = !it.favorite, updatedAt = now()) else it }; persist() }
    fun duplicate(note: Note) { notes = listOf(note.copy(id = UUID.randomUUID().toString(), title = note.title + " Copy", createdAt = now(), updatedAt = now())) + notes; persist(); snackbarMessage = "Note duplicated" }
    fun archive(note: Note) { notes = notes.map { if (it.id == note.id) it.copy(archived = true, trashed = false, updatedAt = now()) else it }; persist(); snackbarMessage = "Moved to archive" }
    fun requestDelete(note: Note) { showDeleteDialog = note }
    fun dismissDeleteDialog() { showDeleteDialog = null }
    fun moveToTrashConfirmed() { val note = showDeleteDialog ?: return; notes = notes.map { if (it.id == note.id) it.copy(trashed = true, archived = false, updatedAt = now()) else it }; showDeleteDialog = null; persist(); snackbarMessage = "Note deleted" }
    fun restore(note: Note) { notes = notes.map { if (it.id == note.id) it.copy(trashed = false, archived = false, updatedAt = now()) else it }; persist(); snackbarMessage = "Note restored" }
    fun deleteForever(note: Note) { notes = notes.filterNot { it.id == note.id }; persist(); snackbarMessage = "Deleted forever" }
    fun emptyTrash() { notes = notes.filterNot { it.trashed }; persist(); snackbarMessage = "Trash emptied" }
    fun exportNotes() { exportMessage = "Exported to: " + store.exportNotes(notes) }
    fun importLastExport() {
        val imported = store.importFromLastExport()
        if (imported.isNotEmpty()) { notes = imported; persist(); exportMessage = "Imported ${imported.size} notes from last export." } else { exportMessage = "No export file found yet." }
    }
}
