package com.creations.bawender.paperdoc

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel : ViewModel() {

    val uiNotes = mutableStateListOf<Note>()

    init {
        Database.notesCollection.watchAll()
            .map { it.toMutableList() }
            .onEach { tasks ->
                uiNotes.clear()
                uiNotes.addAll(tasks)
            }
            .launchIn(viewModelScope)
    }

    fun addRandomNote() = viewModelScope.launch {
        val note = Note(
            text = "Random Note ${Database.notesCollection.count()}",
            createdAt = LocalDate.now()
        )
        Database.notesCollection.setDoc(note.key, note)
    }

    fun deleteNote(key: String) = viewModelScope.launch {
        Database.notesCollection.deleteDoc(key)
    }

}