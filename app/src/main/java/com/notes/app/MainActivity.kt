package com.notes.app

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesTheme {
                NotesApp()
            }
        }
    }
}

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
    HOME, EDITOR, ARCHIVE, TRASH, SETTINGS
}

class NotesStore(private val file: File) {
    fun load(): List<Note> {
        return try {
            if (!file.exists()) return emptyList()

            val array = JSONArray(file.readText())

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

    fun save(notes: List<Note>) {
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

        file.parentFile?.mkdirs()
        file.writeText(array.toString(2))
    }
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val store = NotesStore(File(application.filesDir, "notes.json"))

    var screen by mutableStateOf(Screen.HOME)
        private set

    var selectedNote by mutableStateOf<Note?>(null)
        private set

    var query by mutableStateOf("")
        private set

    var notes by mutableStateOf(loadInitialNotes())
        private set

    private fun loadInitialNotes(): List<Note> {
        val saved = store.load()

        return saved.ifEmpty {
            listOf(
                Note(
                    title = "Welcome to Notes",
                    body = "This app now saves notes permanently on your device using local JSON storage.",
                    folder = "Inbox",
                    tag = "Start",
                    pinned = true
                ),
                Note(
                    title = "Persistent storage enabled",
                    body = "Create a note, close the app, reopen it, and your notes will still be here.",
                    folder = "Productivity",
                    tag = "Storage",
                    favorite = true
                )
            )
        }
    }

    private fun persist() {
        store.save(notes)
    }

    val activeNotes: List<Note>
        get() = notes.filter { !it.trashed && !it.archived }

    val filteredNotes: List<Note>
        get() {
            val base = when (screen) {
                Screen.TRASH -> notes.filter { it.trashed }
                Screen.ARCHIVE -> notes.filter { it.archived && !it.trashed }
                else -> activeNotes
            }

            if (query.isBlank()) return base

            return base.filter {
                it.title.contains(query, true) ||
                    it.body.contains(query, true) ||
                    it.folder.contains(query, true) ||
                    it.tag.contains(query, true)
            }
        }

    fun navigate(target: Screen) {
        screen = target
    }

    fun updateQuery(value: String) {
        query = value
    }

    fun newNote() {
        selectedNote = null
        screen = Screen.EDITOR
    }

    fun edit(note: Note) {
        selectedNote = note
        screen = Screen.EDITOR
    }

    fun save(title: String, body: String, folder: String, tag: String) {
        val cleanTitle = title.ifBlank { "Untitled Note" }
        val cleanBody = body.ifBlank { "No content" }
        val existing = selectedNote

        notes = if (existing == null) {
            listOf(
                Note(
                    title = cleanTitle,
                    body = cleanBody,
                    folder = folder.ifBlank { "Personal" },
                    tag = tag.ifBlank { "General" }
                )
            ) + notes
        } else {
            notes.map {
                if (it.id == existing.id) {
                    it.copy(
                        title = cleanTitle,
                        body = cleanBody,
                        folder = folder.ifBlank { "Personal" },
                        tag = tag.ifBlank { "General" },
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

    fun moveToTrash(note: Note) {
        notes = notes.map {
            if (it.id == note.id) it.copy(trashed = true, archived = false, updatedAt = now()) else it
        }
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

@Composable
fun NotesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF8EA7FF),
            secondary = Color(0xFFB8C7FF),
            background = Color(0xFF10131A),
            surface = Color(0xFF181C25)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(vm: NotesViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Notes", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${vm.activeNotes.size} active notes",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { vm.navigate(Screen.SETTINGS) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            if (vm.screen != Screen.EDITOR) {
                FloatingActionButton(onClick = { vm.newNote() }) {
                    Icon(Icons.Default.Add, contentDescription = "New note")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = vm.screen == Screen.HOME,
                    onClick = { vm.navigate(Screen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = vm.screen == Screen.ARCHIVE,
                    onClick = { vm.navigate(Screen.ARCHIVE) },
                    icon = { Icon(Icons.Default.Archive, contentDescription = null) },
                    label = { Text("Archive") }
                )
                NavigationBarItem(
                    selected = vm.screen == Screen.TRASH,
                    onClick = { vm.navigate(Screen.TRASH) },
                    icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                    label = { Text("Trash") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (vm.screen) {
                Screen.HOME -> HomeScreen(vm)
                Screen.EDITOR -> EditorScreen(vm)
                Screen.ARCHIVE -> NotesListScreen(vm, "Archive")
                Screen.TRASH -> NotesListScreen(vm, "Trash")
                Screen.SETTINGS -> SettingsScreen(vm)
            }
        }
    }
}

@Composable
fun HomeScreen(vm: NotesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Dashboard(vm)

        OutlinedTextField(
            value = vm.query,
            onValueChange = vm::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search notes, folders, or tags") },
            singleLine = true
        )

        NotesListContent(vm, vm.filteredNotes)
    }
}

@Composable
fun Dashboard(vm: NotesViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("Notes", vm.activeNotes.size.toString(), Modifier.weight(1f))
        StatCard("Pinned", vm.activeNotes.count { it.pinned }.toString(), Modifier.weight(1f))
        StatCard("Stars", vm.activeNotes.count { it.favorite }.toString(), Modifier.weight(1f))
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(22.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun NotesListScreen(vm: NotesViewModel, title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        NotesListContent(vm, vm.filteredNotes)
    }
}

@Composable
fun NotesListContent(vm: NotesViewModel, list: List<Note>) {
    if (list.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val sorted = list.sortedWith(compareByDescending<Note> { it.pinned }.thenByDescending { it.updatedAt })
            items(sorted, key = { it.id }) { note ->
                NoteCard(vm, note)
            }
        }
    }
}

@Composable
fun NoteCard(vm: NotesViewModel, note: Note) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        onClick = { vm.edit(note) }
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(note.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(note.updatedAt, style = MaterialTheme.typography.labelSmall)
                }

                if (note.pinned) {
                    Icon(Icons.Default.PushPin, contentDescription = null)
                }
            }

            Text(note.body, style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(note.folder) })
                AssistChip(onClick = {}, label = { Text("#${note.tag}") })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { vm.togglePin(note) }) {
                    Icon(Icons.Default.PushPin, contentDescription = "Pin")
                }

                IconButton(onClick = { vm.toggleFavorite(note) }) {
                    Icon(
                        if (note.favorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite"
                    )
                }

                IconButton(onClick = { vm.duplicate(note) }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate")
                }

                if (note.trashed || note.archived) {
                    IconButton(onClick = { vm.restore(note) }) {
                        Icon(Icons.Default.Restore, contentDescription = "Restore")
                    }
                } else {
                    IconButton(onClick = { vm.archive(note) }) {
                        Icon(Icons.Default.Archive, contentDescription = "Archive")
                    }
                    IconButton(onClick = { vm.moveToTrash(note) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Trash")
                    }
                }

                if (note.trashed) {
                    IconButton(onClick = { vm.deleteForever(note) }) {
                        Icon(Icons.Default.DeleteForever, contentDescription = "Delete forever")
                    }
                }
            }
        }
    }
}

@Composable
fun EditorScreen(vm: NotesViewModel) {
    val editing = vm.selectedNote

    var title by remember(editing?.id) { mutableStateOf(editing?.title ?: "") }
    var body by remember(editing?.id) { mutableStateOf(editing?.body ?: "") }
    var folder by remember(editing?.id) { mutableStateOf(editing?.folder ?: "Personal") }
    var tag by remember(editing?.id) { mutableStateOf(editing?.tag ?: "General") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            if (editing == null) "New Note" else "Edit Note",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Title") },
            singleLine = true
        )

        OutlinedTextField(
            value = folder,
            onValueChange = { folder = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Folder") },
            singleLine = true
        )

        OutlinedTextField(
            value = tag,
            onValueChange = { tag = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Tag") },
            singleLine = true
        )

        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            label = { Text("Body") }
        )

        Button(
            onClick = { vm.save(title, body, folder, tag) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Note")
        }
    }
}

@Composable
fun SettingsScreen(vm: NotesViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Card(shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Offline-first", fontWeight = FontWeight.Bold)
                Text("Notes are now saved permanently on this device using local JSON storage.")
            }
        }

        Card(shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Storage", fontWeight = FontWeight.Bold)
                Text("Data file: notes.json inside private app storage.")
            }
        }

        Button(onClick = { vm.navigate(Screen.HOME) }, modifier = Modifier.fillMaxWidth()) {
            Text("Back Home")
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.NoteAlt, contentDescription = null, modifier = Modifier.size(56.dp))
            Text("No notes here", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Create, restore, or search notes to see them here.")
        }
    }
}
