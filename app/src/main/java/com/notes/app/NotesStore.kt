package com.notes.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class NotesStore(context: Context) {
    private val notesFile = File(context.filesDir, "notes-v4-mvp.json")
    private val backupFile = File(context.filesDir, "notes-v4-mvp-backup.json")
    private val exportFile = File(context.filesDir, "notes-export.json")
    private val prefs = context.getSharedPreferences("notes_v4_mvp_settings", Context.MODE_PRIVATE)

    fun hasCompletedOnboarding(): Boolean = prefs.getBoolean("onboarding_done", false)
    fun setOnboardingDone() { prefs.edit().putBoolean("onboarding_done", true).apply() }
    fun isDarkTheme(): Boolean = prefs.getBoolean("dark_theme", true)
    fun setDarkTheme(value: Boolean) { prefs.edit().putBoolean("dark_theme", value).apply() }
    fun themeName(): String = prefs.getString("theme_name", "Indigo") ?: "Indigo"
    fun setThemeName(value: String) { prefs.edit().putString("theme_name", value).apply() }

    fun viewMode(): ViewMode = when (prefs.getString("view_mode", "LIST")) {
        "GRID" -> ViewMode.GRID
        "COMPACT" -> ViewMode.COMPACT
        else -> ViewMode.LIST
    }
    fun setViewMode(value: ViewMode) { prefs.edit().putString("view_mode", value.name).apply() }

    fun sortMode(): SortMode = when (prefs.getString("sort_mode", "UPDATED")) {
        "CREATED" -> SortMode.CREATED
        "TITLE" -> SortMode.TITLE
        "LABEL" -> SortMode.LABEL
        else -> SortMode.UPDATED
    }
    fun setSortMode(value: SortMode) { prefs.edit().putString("sort_mode", value.name).apply() }

    fun appLockEnabled(): Boolean = prefs.getBoolean("lock_enabled", false)
    fun setAppLockEnabled(value: Boolean) { prefs.edit().putBoolean("lock_enabled", value).apply() }
    fun lockPin(): String = prefs.getString("lock_pin", "1234") ?: "1234"
    fun setLockPin(value: String) { prefs.edit().putString("lock_pin", value).apply() }

    fun loadNotes(): List<Note> = try {
        if (!notesFile.exists()) emptyList() else decodeNotes(notesFile.readText())
    } catch (_: Exception) {
        try { if (!backupFile.exists()) emptyList() else decodeNotes(backupFile.readText()) } catch (_: Exception) { emptyList() }
    }

    fun saveNotes(notes: List<Note>) {
        val encoded = encodeNotes(notes)
        backupFile.writeText(encoded)
        notesFile.writeText(encoded)
    }

    fun exportNotes(notes: List<Note>): String {
        val data = encodeNotes(notes)
        exportFile.writeText(data)
        return exportFile.absolutePath
    }

    fun importFromLastExport(): List<Note> = try {
        if (!exportFile.exists()) emptyList() else decodeNotes(exportFile.readText())
    } catch (_: Exception) { emptyList() }

    private fun encodeNotes(notes: List<Note>): String {
        val array = JSONArray()
        notes.forEach { note ->
            array.put(JSONObject().apply {
                put("id", note.id)
                put("title", note.title)
                put("body", note.body)
                put("label", note.label)
                put("tags", note.tags)
                put("color", note.color)
                put("pinned", note.pinned)
                put("favorite", note.favorite)
                put("archived", note.archived)
                put("trashed", note.trashed)
                put("locked", note.locked)
                put("createdAt", note.createdAt)
                put("updatedAt", note.updatedAt)
            })
        }
        return array.toString(2)
    }

    private fun decodeNotes(raw: String): List<Note> {
        val array = JSONArray(raw)
        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
            val updated = item.optString("updatedAt", now())
            Note(
                id = item.optString("id", UUID.randomUUID().toString()),
                title = item.optString("title", "Untitled Note"),
                body = item.optString("body", ""),
                label = item.optString("label", item.optString("folder", "Personal")),
                tags = item.optString("tags", item.optString("tag", "General")),
                color = item.optLong("color", 0xFF8EA7FF),
                pinned = item.optBoolean("pinned", false),
                favorite = item.optBoolean("favorite", false),
                archived = item.optBoolean("archived", false),
                trashed = item.optBoolean("trashed", false),
                locked = item.optBoolean("locked", false),
                createdAt = item.optString("createdAt", updated),
                updatedAt = updated
            )
        }
    }
}
