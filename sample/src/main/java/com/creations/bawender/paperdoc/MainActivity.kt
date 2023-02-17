@file:OptIn(ExperimentalMaterialApi::class)

package com.creations.bawender.paperdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.creations.bawender.paperdoc.ui.theme.PaperDocTheme

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaperDocTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NotesUI(vm.uiNotes, vm::deleteNote, vm::addRandomNote)
                }
            }
        }
    }
}

@Composable
fun NotesUI(
    notes: List<Note>,
    onRemove: (String) -> Unit,
    onAdd: () -> Unit
) {
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(text = "PaperDoc Notes Sample") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "add")
            }
        }
    ) { values ->

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(values)
        ) {

            items(notes) {
                ListItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = { Text(text = it.text) },
                    secondaryText = { Text(text = it.createdAt.toString()) },
                    trailing = {
                        IconButton(onClick = { onRemove(it.key) }) {
                            Icon(Icons.Default.Delete, contentDescription = "remove")
                        }
                    }
                )
                Divider()
            }

        }
    }
}