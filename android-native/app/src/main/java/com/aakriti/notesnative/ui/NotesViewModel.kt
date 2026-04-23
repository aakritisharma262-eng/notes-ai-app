package com.aakriti.notesnative.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aakriti.notesnative.data.NotesViewMode
import com.aakriti.notesnative.data.SettingsStore
import com.aakriti.notesnative.ui.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotesUiState(
  val query: String = "",
  val viewMode: NotesViewMode = NotesViewMode.Grid,
  val notes: List<Note> = emptyList(),
) {
  val countLabel: String get() = "${notes.size} ${if (notes.size == 1) "note" else "notes"}"
  val filtered: List<Note>
    get() {
      val q = query.trim().lowercase()
      if (q.isEmpty()) return notes
      return notes.filter { n ->
        n.title.lowercase().contains(q) || n.content.lowercase().contains(q)
      }
    }
}

class NotesViewModel(
  private val repo: NotesRepository,
  private val settings: SettingsStore,
) : ViewModel() {
  private val queryFlow = kotlinx.coroutines.flow.MutableStateFlow("")

  val state: StateFlow<NotesUiState> =
    combine(repo.observeNotes(), settings.viewMode, queryFlow) { notes, mode, query ->
      NotesUiState(query = query, viewMode = mode, notes = notes)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotesUiState())

  fun setQuery(q: String) {
    queryFlow.value = q
  }

  fun toggleViewMode() {
    viewModelScope.launch {
      val next = if (state.value.viewMode == NotesViewMode.Grid) NotesViewMode.List else NotesViewMode.Grid
      settings.setViewMode(next)
    }
  }

  fun setViewMode(mode: NotesViewMode) {
    viewModelScope.launch { settings.setViewMode(mode) }
  }

  fun togglePin(note: Note) {
    viewModelScope.launch {
      repo.upsert(note.copy(pinned = !note.pinned, updatedAt = System.currentTimeMillis()))
    }
  }

  fun delete(note: Note) {
    viewModelScope.launch { repo.delete(note.id) }
  }

  fun upsert(note: Note) {
    viewModelScope.launch { repo.upsert(note) }
  }
}

class NotesViewModelFactory(
  private val repo: NotesRepository,
  private val settings: SettingsStore,
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return NotesViewModel(repo, settings) as T
  }
}

