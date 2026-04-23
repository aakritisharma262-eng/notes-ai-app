package com.aakriti.notesnative.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
  primary = NotesColor.Primary,
  secondary = NotesColor.Secondary,
  background = NotesColor.Background,
  surface = NotesColor.Surface,
  onPrimary = NotesColor.OnPrimary,
  onSecondary = NotesColor.OnSecondary,
  onBackground = NotesColor.OnBackground,
  onSurface = NotesColor.OnSurface,
)

private val DarkColors = darkColorScheme(
  primary = NotesColor.PrimaryDark,
  secondary = NotesColor.SecondaryDark,
  background = NotesColor.BackgroundDark,
  surface = NotesColor.SurfaceDark,
  onPrimary = NotesColor.OnPrimaryDark,
  onSecondary = NotesColor.OnSecondaryDark,
  onBackground = NotesColor.OnBackgroundDark,
  onSurface = NotesColor.OnSurfaceDark,
)

@Composable
fun NotesTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColors else LightColors,
    typography = NotesTypography,
    content = content,
  )
}

