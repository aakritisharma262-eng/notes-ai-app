package com.aakriti.notesnative.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
  @PrimaryKey val id: String,
  val title: String,
  val content: String,
  val color: String,
  val pinned: Boolean,
  val createdAt: Long,
  val updatedAt: Long,
)

