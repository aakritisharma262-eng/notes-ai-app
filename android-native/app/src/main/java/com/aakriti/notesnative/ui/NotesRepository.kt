package com.aakriti.notesnative.ui

import com.aakriti.notesnative.data.NoteDao
import com.aakriti.notesnative.ui.model.Note
import com.aakriti.notesnative.ui.model.NoteColor
import com.aakriti.notesnative.ui.model.toEntity
import com.aakriti.notesnative.ui.model.toUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class NotesRepository(private val dao: NoteDao) {
  fun observeNotes(): Flow<List<Note>> = dao.observeAll().map { list -> list.map { it.toUi() } }

  suspend fun getById(id: String): Note? = dao.getById(id)?.toUi()

  suspend fun upsert(note: Note) = dao.upsert(note.toEntity())

  suspend fun delete(id: String) = dao.deleteById(id)

  fun newNote(now: Long = System.currentTimeMillis()): Note {
    val id = "$now-${Random.nextInt(1000, 9999)}"
    val colors = listOf(NoteColor.Sky, NoteColor.Mint, NoteColor.Lavender, NoteColor.Blush, NoteColor.Apricot)
    return Note(
      id = id,
      title = "",
      content = "",
      color = colors.random(),
      pinned = false,
      createdAt = now,
      updatedAt = now,
    )
  }
}

