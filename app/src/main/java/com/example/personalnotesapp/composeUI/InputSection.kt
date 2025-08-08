package com.example.personalnotesapp.composeUI

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.personalnotesapp.viewModels.NotesViewModel
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent

@Composable
fun NoteInputSection(
    viewModel: NotesViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit
    ){
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    var coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ){
        OutlinedTextField(
            value = title,
            onValueChange = {title = it},
            label = {Text("Note Title")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = {content = it},
            label = {Text("Note Content")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ){
            //Save button
            Button(
                onClick = {
                if (title.isNotEmpty() || content.isNotEmpty()){
                    coroutineScope.launch {
                        viewModel.saveNote(title, content)
                        //Clear after saving
                        title = ""
                        content = ""
                        onSave()
                    }
                }else{
                    onCancel()
                }
            },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF800080)
                )
                ) {
                Text(text = "Save Note")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                title = ""
                content = ""
                onCancel()
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF800080),
                    contentColor = Color.White
                )
                ) {
                Text(text = "Cancel")
            }
        }

    }
}