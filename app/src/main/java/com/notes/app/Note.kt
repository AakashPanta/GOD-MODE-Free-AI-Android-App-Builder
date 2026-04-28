package com.notes.app

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val folder: String = "Personal",
    val tag: String = "General",
    val pinned: Boolean = false,
    val favorite: Boolean = false,
    val archived: Boolean = false,
    val trashed: Boolean = false,
    val updatedAt: String = now()
)

fun now(): String {
    return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
}

enum class Screen {
    ONBOARDING, HOME, EDITOR, FAVORITES, ARCHIVE, TRASH, SETTINGS
}
