package com.aakriti.notesnative.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aakriti.notesnative.ui.NotesViewModel

@Composable
fun NoteEditorScreen(
  vm: NotesViewModel,
  noteId: String,
  onBack: () -> Unit,
) {
  var title by remember { mutableStateOf("") }
  var content by remember { mutableStateOf("") }
  var pinned by remember { mutableStateOf(false) }

  LaunchedEffect(noteId) {
    val note = vm.state.value.notes.firstOrNull { it.id == noteId }
    title = note?.title.orEmpty()
    content = note?.content.orEmpty()
    pinned = note?.pinned ?: false
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.Top,
  ) {
    Row(
      modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      IconButton(onClick = {
        // Autosave on back
        val existing = vm.state.value.notes.firstOrNull { it.id == noteId }
        val now = System.currentTimeMillis()
        if (existing != null) {
          vm.upsert(existing.copy(title = title, content = content, pinned = pinned, updatedAt = now))
        }
        onBack()
      }) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { pinned = !pinned }) {
          Icon(
            imageVector = if (pinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
            contentDescription = "Pin",
          )
        }
        IconButton(onClick = {
          val note = vm.state.value.notes.firstOrNull { it.id == noteId } ?: return@IconButton
          vm.delete(note)
          onBack()
        }) {
          Icon(Icons.Filled.Delete, contentDescription = "Delete")
        }
        TextButton(onClick = {
          val existing = vm.state.value.notes.firstOrNull { it.id == noteId }
          val now = System.currentTimeMillis()
          if (existing != null) {
            vm.upsert(existing.copy(title = title, content = content, pinned = pinned, updatedAt = now))
          }
          onBack()
        }) {
          Text("Save")
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
      value = title,
      onValueChange = { title = it },
      modifier = Modifier.fillMaxSize(),
      textStyle = MaterialTheme.typography.headlineLarge,
      placeholder = { Text("Title") },
      shape = RoundedCornerShape(16.dp),
      singleLine = false,
    )

    Spacer(modifier = Modifier.size(12.dp))

    OutlinedTextField(
      value = content,
      onValueChange = { content = it },
      modifier = Modifier
        .fillMaxSize()
        .weight(1f),
      textStyle = MaterialTheme.typography.bodyMedium,
      placeholder = { Text("Write your note...") },
      shape = RoundedCornerShape(16.dp),
    )
  }
}

