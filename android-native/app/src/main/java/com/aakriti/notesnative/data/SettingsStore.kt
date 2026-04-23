package com.aakriti.notesnative.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class NotesViewMode { Grid, List }

private val Context.dataStore by preferencesDataStore(name = "notes_settings")

class SettingsStore(private val context: Context) {
  private val viewKey = stringPreferencesKey("view_mode")

  val viewMode: Flow<NotesViewMode> =
    context.dataStore.data.map { prefs: Preferences ->
      when (prefs[viewKey]) {
        "list" -> NotesViewMode.List
        else -> NotesViewMode.Grid
      }
    }

  suspend fun setViewMode(mode: NotesViewMode) {
    context.dataStore.edit { it[viewKey] = if (mode == NotesViewMode.List) "list" else "grid" }
  }
}

