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

    var screen by mutableStateOf(
        if (store.appLockEnabled()) Screen.LOCK else if (store.hasCompletedOnboarding()) Screen.HOME else Screen.ONBOARDING
    )
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

    var notes by mutableStateOf(loadInitialNotes())
        private set

    private fun loadInitialNotes(): List<Note> {
        val saved = store.loadNotes()
        return saved.ifEmpty {
            listOf(
                Note(
                    title = "Welcome to Notes V3",
                    body = "V3 adds labels, lock screen, formatting helpers, import/export, view modes, sort modes, and material themes.",
                    label = "Inbox",
                    tags = "Start, V3",
                    pinned = true
                ),
                Note(
                    title = "Formatting toolbar",
                    body = "Use the editor buttons to insert bold, italic, underline, headings, quotes, links, bullets, numbered lists, dividers, and highlights.",
                    label = "Writing",
                    tags = "Editor",
                    favorite = true
                )
            )
        }
    }

    private fun persist() {
        store.saveNotes(notes)
    }

    val labels: List<String>
        get() = listOf("All") + notes
            .filter { !it.trashed && !it.archived }
            .map { it.label.ifBlank { "Personal" } }
            .distinct()
            .sorted()

    val activeNotes: List<Note>
        get() = notes.filter { !it.trashed && !it.archived }

    val pinnedNotes: List<Note>
        get() = activeNotes.filter { it.pinned }

    val filteredNotes: List<Note>
        get() {
            val base = when (screen) {
                Screen.TRASH -> notes.filter { it.trashed }
                Screen.ARCHIVE -> notes.filter { it.archived && !it.trashed }
                Screen.FAVORITES -> activeNotes.filter { it.favorite }
                else -> activeNotes
            }.let { list ->
                if (selectedLabel == "All") list else list.filter { it.label == selectedLabel }
            }

            val searched = if (query.isBlank()) {
                base
            } else {
                base.filter {
                    it.title.contains(query, true) ||
                        it.body.contains(query, true) ||
                        it.label.contains(query, true) ||
                        it.tags.contains(query, true)
                }
            }

            return when (sortMode) {
                SortMode.TITLE -> searched.sortedBy { it.title.lowercase() }
                SortMode.LABEL -> searched.sortedBy { it.label.lowercase() }
                SortMode.UPDATED -> searched.sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.updatedAt })
            }
        }

    fun unlock(pin: String): Boolean {
        val ok = pin == store.lockPin()
        if (ok) {
            unlocked = true
            screen = if (store.hasCompletedOnboarding()) Screen.HOME else Screen.ONBOARDING
        }
        return ok
    }

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

    fun selectLabel(value: String) {
        selectedLabel = value
    }

    fun toggleTheme() {
        darkTheme = !darkTheme
        store.setDarkTheme(darkTheme)
    }

    fun setTheme(value: String) {
        themeName = value
        store.setThemeName(value)
    }

    fun toggleViewMode() {
        viewMode = if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
        store.setViewMode(viewMode)
    }

    fun setSort(value: SortMode) {
        sortMode = value
        store.setSortMode(value)
    }

    fun setLock(enabled: Boolean, pin: String) {
        store.setAppLockEnabled(enabled)
        if (pin.length >= 4) store.setLockPin(pin)
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

    fun save(title: String, body: String, label: String, tags: String, color: Long, locked: Boolean) {
        val cleanTitle = title.ifBlank { "Untitled Note" }
        val cleanBody = body.ifBlank { "No content" }
        val cleanLabel = label.ifBlank { "Personal" }
        val cleanTags = tags.ifBlank { "General" }
        val existing = selectedNote

        notes = if (existing == null) {
            listOf(
                Note(
                    title = cleanTitle,
                    body = cleanBody,
                    label = cleanLabel,
                    tags = cleanTags,
                    color = color,
                    locked = locked
                )
            ) + notes
        } else {
            notes.map {
                if (it.id == existing.id) {
                    it.copy(
                        title = cleanTitle,
                        body = cleanBody,
                        label = cleanLabel,
                        tags = cleanTags,
                        color = color,
                        locked = locked,
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
        notes = notes.map { if (it.id == note.id) it.copy(pinned = !it.pinned, updatedAt = now()) else it }
        persist()
    }

    fun toggleFavorite(note: Note) {
        notes = notes.map { if (it.id == note.id) it.copy(favorite = !it.favorite, updatedAt = now()) else it }
        persist()
    }

    fun duplicate(note: Note) {
        notes = listOf(note.copy(id = UUID.randomUUID().toString(), title = note.title + " Copy", updatedAt = now())) + notes
        persist()
    }

    fun archive(note: Note) {
        notes = notes.map { if (it.id == note.id) it.copy(archived = true, trashed = false, updatedAt = now()) else it }
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
        notes = notes.map { if (it.id == note.id) it.copy(trashed = true, archived = false, updatedAt = now()) else it }
        showDeleteDialog = null
        persist()
    }

    fun restore(note: Note) {
        notes = notes.map { if (it.id == note.id) it.copy(trashed = false, archived = false, updatedAt = now()) else it }
        persist()
    }

    fun deleteForever(note: Note) {
        notes = notes.filterNot { it.id == note.id }
        persist()
    }

    fun exportNotes() {
        exportMessage = "Exported to: " + store.exportNotes(notes)
    }

    fun importLastExport() {
        val imported = store.importFromLastExport()
        if (imported.isNotEmpty()) {
            notes = imported
            persist()
            exportMessage = "Imported ${imported.size} notes from last export."
        } else {
            exportMessage = "No export file found yet."
        }
    }
}
