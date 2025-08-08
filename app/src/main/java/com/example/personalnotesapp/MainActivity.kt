package com.example.personalnotesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalnotesapp.data.Note
import com.example.personalnotesapp.data.NotesRepository
import com.example.personalnotesapp.viewModels.NotesViewModel
import com.example.personalnotesapp.viewModels.NotesViewModelFactory
import com.example.personalnotesapp.composeUI.NotesAppScreen
import com.example.personalnotesapp.ui.theme.PersonalNotesAppTheme
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

// Define Realm and NotesRepository outside the class for application-level access
lateinit var realm: Realm
private lateinit var notesRepository: NotesRepository

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize RealmConfiguration, Realm, and Repository
        val config = RealmConfiguration.Builder(schema = setOf(Note::class))
            .deleteRealmIfMigrationNeeded()
            .build()

        realm = Realm.open(config)
        notesRepository = NotesRepository(realm)

        setContent {
            // Initialize ViewModel inside setContent using the provided factory.
            val notesViewModel: NotesViewModel = viewModel(
                factory = NotesViewModelFactory(notesRepository)
            )

            // Observe the Dark Mode state from the initialized ViewModel instance.
            // This is done within the Composable scope.
            val isDarkModeEnabled by notesViewModel.isDarkModeEnabled.collectAsState()

            // Pass the observed dark mode state to the theme composable.
            PersonalNotesAppTheme(darkTheme = isDarkModeEnabled) {
                // Pass the ViewModel instance to the main screen.
                NotesAppScreen(notesViewModel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Ensure Realm is closed when the activity is destroyed
        if (!realm.isClosed()) {
            realm.close()
        }
    }
}