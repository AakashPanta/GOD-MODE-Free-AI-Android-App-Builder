package com.notes.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class NotesStore(context: Context) {
    private val notesFile = File(context.filesDir, "notes-v3.json")
    private val exportFile = File(context.filesDir, "notes-export.json")
    private val prefs = context.getSharedPreferences("notes_v3_settings", Context.MODE_PRIVATE)

    fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean("onboarding_done", false)
    }

    fun setOnboardingDone() {
        prefs.edit().putBoolean("onboarding_done", true).apply()
    }

    fun isDarkTheme(): Boolean {
        return prefs.getBoolean("dark_theme", true)
    }

    fun setDarkTheme(value: Boolean) {
        prefs.edit().putBoolean("dark_theme", value).apply()
    }

    fun themeName(): String {
        return prefs.getString("theme_name", "Indigo") ?: "Indigo"
    }

    fun setThemeName(value: String) {
        prefs.edit().putString("theme_name", value).apply()
    }

    fun viewMode(): ViewMode {
        return if (prefs.getString("view_mode", "LIST") == "GRID") ViewMode.GRID else ViewMode.LIST
    }

    fun setViewMode(value: ViewMode) {
        prefs.edit().putString("view_mode", value.name).apply()
    }

    fun sortMode(): SortMode {
        return when (prefs.getString("sort_mode", "UPDATED")) {
            "TITLE" -> SortMode.TITLE
            "LABEL" -> SortMode.LABEL
            else -> SortMode.UPDATED
        }
    }

    fun setSortMode(value: SortMode) {
        prefs.edit().putString("sort_mode", value.name).apply()
    }

    fun appLockEnabled(): Boolean {
        return prefs.getBoolean("lock_enabled", false)
    }

    fun setAppLockEnabled(value: Boolean) {
        prefs.edit().putBoolean("lock_enabled", value).apply()
    }

    fun lockPin(): String {
        return prefs.getString("lock_pin", "1234") ?: "1234"
    }

    fun setLockPin(value: String) {
        prefs.edit().putString("lock_pin", value).apply()
    }

    fun loadNotes(): List<Note> {
        return try {
            if (!notesFile.exists()) return emptyList()
            decodeNotes(notesFile.readText())
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveNotes(notes: List<Note>) {
        notesFile.writeText(encodeNotes(notes))
    }

    fun exportNotes(notes: List<Note>): String {
        val data = encodeNotes(notes)
        exportFile.writeText(data)
        return exportFile.absolutePath
    }

    fun importFromLastExport(): List<Note> {
        return try {
            if (!exportFile.exists()) emptyList() else decodeNotes(exportFile.readText())
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun encodeNotes(notes: List<Note>): String {
        val array = JSONArray()
        notes.forEach { note ->
            array.put(
                JSONObject().apply {
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
                    put("updatedAt", note.updatedAt)
                }
            )
        }
        return array.toString(2)
    }

    private fun decodeNotes(raw: String): List<Note> {
        val array = JSONArray(raw)

        return List(array.length()) { index ->
            val item = array.getJSONObject(index)
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
                updatedAt = item.optString("updatedAt", now())
            )
        }
    }
}
