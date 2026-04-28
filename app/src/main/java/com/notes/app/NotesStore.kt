package com.notes.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class NotesStore(context: Context) {
    private val notesFile = File(context.filesDir, "notes.json")
    private val prefs = context.getSharedPreferences("notes_settings", Context.MODE_PRIVATE)

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

    fun loadNotes(): List<Note> {
        return try {
            if (!notesFile.exists()) return emptyList()
            val array = JSONArray(notesFile.readText())

            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                Note(
                    id = item.optString("id", UUID.randomUUID().toString()),
                    title = item.optString("title", "Untitled Note"),
                    body = item.optString("body", ""),
                    folder = item.optString("folder", "Personal"),
                    tag = item.optString("tag", "General"),
                    pinned = item.optBoolean("pinned", false),
                    favorite = item.optBoolean("favorite", false),
                    archived = item.optBoolean("archived", false),
                    trashed = item.optBoolean("trashed", false),
                    updatedAt = item.optString("updatedAt", now())
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveNotes(notes: List<Note>) {
        val array = JSONArray()
        notes.forEach { note ->
            array.put(
                JSONObject().apply {
                    put("id", note.id)
                    put("title", note.title)
                    put("body", note.body)
                    put("folder", note.folder)
                    put("tag", note.tag)
                    put("pinned", note.pinned)
                    put("favorite", note.favorite)
                    put("archived", note.archived)
                    put("trashed", note.trashed)
                    put("updatedAt", note.updatedAt)
                }
            )
        }
        notesFile.writeText(array.toString(2))
    }
}
