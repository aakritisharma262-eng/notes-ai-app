package com.aakriti.notesnative.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [NoteEntity::class],
  version = 1,
  exportSchema = false,
)
abstract class NotesDb : RoomDatabase() {
  abstract fun noteDao(): NoteDao
}

