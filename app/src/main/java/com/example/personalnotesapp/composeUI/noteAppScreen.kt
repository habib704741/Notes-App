package com.example.personalnotesapp.composeUI

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.personalnotesapp.data.Note
import com.example.personalnotesapp.viewModels.NotesViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesAppScreen(viewModel: NotesViewModel) {
    // 1. Observe the notes list from the ViewModel using 'collectAsState'
    val notesList by viewModel.notes.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()

    //Editing of note managing state
    var selectedNoteForEdit by remember { mutableStateOf<Note?>(null) }
    var showEditDialog by remember {mutableStateOf(false)}

    //Input the note when add
    var showInputSection by remember { mutableStateOf(false) }

    //Display the note
    var showNoteDetailScreen by remember { mutableStateOf(false) }


    //coroutine scop
    val coroutineScope = rememberCoroutineScope()

    // Determine the content to display based on the state
    val isViewingNoteDetail = showNoteDetailScreen && selectedNoteForEdit != null
    val isViewingNotesList = !showInputSection && !isViewingNoteDetail

    // State to control the dropdown menu visibility.
    var expanded by remember { mutableStateOf(false) }

    //Dark mode state from view model
    val isDarkModeEnabled by viewModel.isDarkModeEnabled.collectAsState()

    //Back Stack Handling for note detail screen
    BackHandler(enabled = isViewingNoteDetail){
        showNoteDetailScreen = false
        selectedNoteForEdit = null
    }

    //Back Stack handling for note input screen
    BackHandler (enabled = showInputSection){
        showInputSection = false
    }

    Scaffold(
        topBar = { TopAppBar(
            title = { Text("Personal Notes") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF800080),
                titleContentColor = Color.White
            ),
            actions = {
                IconButton(onClick = {expanded = true}) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More Options", tint = Color.White)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false}
                ) {
                    DropdownMenuItem(
                        text = {
                            // Display current status
                            Text("Dark Mode: ${if (isDarkModeEnabled) "On" else "Off"}")
                        },
                        onClick = {
                            viewModel.toggleDarkMode()
                            expanded = false
                        }
                    )
                }
            }
        ) },
        floatingActionButton = {
            if (isViewingNoteDetail){
                FloatingActionButton(onClick = {
                    showNoteDetailScreen = false
                    showEditDialog = true
                },
                    contentColor = Color.White,
                    containerColor = Color(0xFF800080)
                    ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit note")
                }
            }else if (isViewingNotesList){
                FloatingActionButton(onClick = {
                    showInputSection = true
                },
                    contentColor = Color.White,
                    containerColor = Color(0xFF800080)
                    ) {
                    Icon(
                        Icons.Filled.Add, contentDescription = "Add note",
                    )
                }
            }
        }
    ) { paddingValues ->
        if (showInputSection){
            Box(modifier = Modifier.padding(paddingValues)){
                NoteInputSection(
                    viewModel = viewModel,
                    onSave = {
                        showInputSection = false
                    },
                    onCancel = {
                        showInputSection = false
                    }
                )
            }
        }else if (isViewingNoteDetail){
            Box(modifier = Modifier.padding(paddingValues)){
                NoteDetailedScreen(
                    note = selectedNoteForEdit!!,
                    onEditClicked = {note ->
                        showNoteDetailScreen = false
                        showEditDialog = true
                    },
                    onBackedClicked = {
                        showNoteDetailScreen = false
                        selectedNoteForEdit = null
                    }
                )
            }
        }else if (showNoteDetailScreen && selectedNoteForEdit != null) {
            NoteDetailedScreen(
                note = selectedNoteForEdit!!,
                onEditClicked = {note ->
                    showNoteDetailScreen = false
                    showEditDialog = true
                },
                onBackedClicked = {
                    showNoteDetailScreen = false
                    selectedNoteForEdit = null
                }
            )
        }
        else{
            Column(modifier = Modifier.padding(paddingValues)) {

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = { },
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
                    placeholder = {Text("Search Notes")},
                    leadingIcon = {Icon(Icons.Filled.Search, contentDescription = null)},
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()){
                            IconButton(onClick = {viewModel.setSearchQuery("")}) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Search")
                            }
                        }
                    }
                ) { }

                if (notesList.isEmpty()){
                    NoNotesMessage(modifier  = Modifier.fillMaxWidth())
                }else{
                    NotesList(
                        notes = notesList,
                        onDeleteNote = { note ->
                            coroutineScope.launch { viewModel.deleteNote(note) }
                        },
                        onNoteClicked = { note ->
                            selectedNoteForEdit = note
                            showNoteDetailScreen = true
                        }
                    )
                }
            }
        }

    }


    //show the edit dialog
    if (showEditDialog && selectedNoteForEdit != null){
        EditNoteDialog(
            note = selectedNoteForEdit!!,
            onDismiss = {
                showEditDialog = false
                selectedNoteForEdit = null
                        },
            onSave = {updatedTitle, updatedContent ->
                val noteSelectedToUpdate = selectedNoteForEdit
                coroutineScope.launch {
                    noteSelectedToUpdate?.let { note ->
                        viewModel.updateNote(
                            note = note,
                            newTitle = updatedTitle,
                            newContent = updatedContent
                        )
                    }
                }
                showEditDialog = false
                selectedNoteForEdit = null
            }
        )
    }
}

@Composable
fun NoNotesMessage(modifier: Modifier = Modifier) {
    // You can replace this with an actual image or illustration later if desired
    Box(
        modifier = modifier.padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Notes Found. Click the '+' button to add a new note.",
                textAlign = TextAlign.Center
            )
        }
    }
}