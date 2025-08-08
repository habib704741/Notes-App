package com.example.personalnotesapp.composeUI

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.personalnotesapp.data.Note

@Composable
fun NotesList(notes: List<Note>,
              onDeleteNote: (Note) -> Unit,
              onNoteClicked: (Note) -> Unit,
              ){
    LazyColumn (contentPadding = PaddingValues(16.dp)){
        items(notes){note ->
            NoteCard(
                note = note,
                onDeleteClicked = onDeleteNote,
                onCardClicked = {onNoteClicked(note)}

            )
        }
    }
}