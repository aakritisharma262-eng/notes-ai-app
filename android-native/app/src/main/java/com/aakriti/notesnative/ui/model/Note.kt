package com.aakriti.notesnative.ui.model

data class Note(
  val id: String,
  val title: String,
  val content: String,
  val color: NoteColor,
  val pinned: Boolean,
  val createdAt: Long,
  val updatedAt: Long,
)

enum class NoteColor { Sky, Mint, Lavender, Blush, Apricot }

