package com.aakriti.notesnative.ui.model

import com.aakriti.notesnative.data.NoteEntity

fun NoteEntity.toUi(): Note {
  return Note(
    id = id,
    title = title,
    content = content,
    color = when (color) {
      "mint" -> NoteColor.Mint
      "lavender" -> NoteColor.Lavender
      "blush" -> NoteColor.Blush
      "apricot" -> NoteColor.Apricot
      else -> NoteColor.Sky
    },
    pinned = pinned,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )
}

fun Note.toEntity(): NoteEntity {
  return NoteEntity(
    id = id,
    title = title,
    content = content,
    color = when (color) {
      NoteColor.Mint -> "mint"
      NoteColor.Lavender -> "lavender"
      NoteColor.Blush -> "blush"
      NoteColor.Apricot -> "apricot"
      NoteColor.Sky -> "sky"
    },
    pinned = pinned,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )
}

