package com.aakriti.notesnative

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aakriti.notesnative.ui.App
import com.aakriti.notesnative.ui.theme.NotesTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      NotesTheme {
        App()
      }
    }
  }
}

