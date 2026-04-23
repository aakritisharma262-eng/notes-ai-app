package com.aakriti.notesnative.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
  @Query(
    """
      SELECT * FROM notes
      ORDER BY pinned DESC, updatedAt DESC
    """,
  )
  fun observeAll(): Flow<List<NoteEntity>>

  @Query(
    """
      SELECT * FROM notes
      WHERE id = :id
      LIMIT 1
    """,
  )
  suspend fun getById(id: String): NoteEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(note: NoteEntity)

  @Query("DELETE FROM notes WHERE id = :id")
  suspend fun deleteById(id: String)
}

