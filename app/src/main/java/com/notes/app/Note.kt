package com.notes.app

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val label: String = "Personal",
    val tags: String = "General",
    val color: Long = 0xFF8EA7FF,
    val pinned: Boolean = false,
    val favorite: Boolean = false,
    val archived: Boolean = false,
    val trashed: Boolean = false,
    val locked: Boolean = false,
    val updatedAt: String = now()
)

fun now(): String {
    return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
}

enum class Screen {
    LOCK, ONBOARDING, HOME, EDITOR, LABELS, FAVORITES, ARCHIVE, TRASH, SETTINGS, EXPORT_IMPORT, ABOUT
}

enum class ViewMode {
    LIST, GRID
}

enum class SortMode {
    UPDATED, TITLE, LABEL
}
