package com.aakriti.notesnative.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aakriti.notesnative.data.NotesViewMode
import com.aakriti.notesnative.ui.NotesViewModel
import com.aakriti.notesnative.ui.components.NoteCard
import com.aakriti.notesnative.ui.components.NotesHeader
import com.aakriti.notesnative.ui.components.NotesSearchBar
import com.aakriti.notesnative.ui.model.NoteColor
import com.aakriti.notesnative.ui.theme.NoteCardColor

@Composable
fun NotesHomeScreen(
  vm: NotesViewModel,
  onOpenNote: (String) -> Unit,
  onCreateNote: () -> Unit,
) {
  val state by vm.state.collectAsState()
  val filtered = state.filtered

  Scaffold(
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      NotesHeader(
        title = "Notes",
        subtitle = state.countLabel,
        rightIcon = Icons.Filled.Person,
        onRightIconClick = { /* TODO settings/profile */ },
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onCreateNote,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ) {
        Text("+", style = MaterialTheme.typography.titleLarge)
      }
    },
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding),
    ) {
      NotesSearchBar(
        value = state.query,
        onValueChange = vm::setQuery,
        placeholder = "Search notes...",
        leadingIcon = Icons.Filled.Search,
        modifier = Modifier.padding(top = 4.dp),
      )

      if (filtered.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = if (state.query.isBlank()) "No notes yet" else "No results",
            style = MaterialTheme.typography.titleLarge,
          )
        }
      } else {
        val cols = if (state.viewMode == NotesViewMode.Grid) 2 else 1
        LazyVerticalGrid(
          columns = GridCells.Fixed(cols),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 80.dp, bottom = 96.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          items(filtered, key = { it.id }) { note ->
            NoteCard(
              note = note,
              background = when (note.color) {
                NoteColor.Sky -> NoteCardColor.Sky
                NoteColor.Mint -> NoteCardColor.Mint
                NoteColor.Lavender -> NoteCardColor.Lavender
                NoteColor.Blush -> NoteCardColor.Blush
                NoteColor.Apricot -> NoteCardColor.Apricot
              },
              pinnedIcon = Icons.Filled.PushPin,
              overflowIcon = Icons.Filled.MoreVert,
              onOverflow = { /* TODO menu */ },
              onClick = { onOpenNote(note.id) },
              modifier = Modifier,
            )
          }
        }
      }

      // View toggle (minimal, top-right-ish overlay under header)
      androidx.compose.material3.SegmentedButtonRow(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = 72.dp, end = 16.dp),
      ) {
        androidx.compose.material3.SegmentedButton(
          selected = state.viewMode == NotesViewMode.Grid,
          onClick = { vm.setViewMode(NotesViewMode.Grid) },
          shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(index = 0, count = 2),
          icon = { Icon(Icons.Filled.GridView, contentDescription = "Grid") },
        ) {}
        androidx.compose.material3.SegmentedButton(
          selected = state.viewMode == NotesViewMode.List,
          onClick = { vm.setViewMode(NotesViewMode.List) },
          shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(index = 1, count = 2),
          icon = { Icon(Icons.Filled.List, contentDescription = "List") },
        ) {}
      }
    }
  }
}

