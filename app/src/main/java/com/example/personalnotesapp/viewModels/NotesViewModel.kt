package com.example.personalnotesapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalnotesapp.data.Note
import com.example.personalnotesapp.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class NotesViewModel(private val repository: NotesRepository): ViewModel() {

//    val notes = repository.getAllNotes()

    // MutableStateFlow to hold the current search query
    private val _searchQuery = MutableStateFlow("")
    // Expose the search query as a read-only StateFlow
    val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    val notes: StateFlow<List<Note>> = repository.getAllNotes()
        .combine(_searchQuery){allNotes, query ->
            if (query.isBlank()){
                allNotes
            }else{
                allNotes.filter { note ->
                    note.title.contains(query, ignoreCase = true) || note.content.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isDarkModeEnabled = MutableStateFlow(false)
    val isDarkModeEnabled: StateFlow<Boolean> = _isDarkModeEnabled.asStateFlow()

    //state flow for dark mode
    fun toggleDarkMode() {
        _isDarkModeEnabled.value = !_isDarkModeEnabled.value
    }

    fun setSearchQuery(query: String){
        _searchQuery.value = query
    }

    suspend fun saveNote(title: String, content: String){
        repository.addNote(title, content)
    }

    suspend fun deleteNote(note: Note){
        repository.deleteNote(note)
    }
    suspend fun updateNote(note: Note, newTitle: String, newContent: String) {
        repository.updateNote(note, newTitle, newContent, null)
    }

}