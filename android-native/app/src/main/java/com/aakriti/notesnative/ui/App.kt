package com.aakriti.notesnative.ui

import android.app.Application
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.aakriti.notesnative.data.NotesDb
import com.aakriti.notesnative.data.SettingsStore
import com.aakriti.notesnative.ui.screens.NoteEditorScreen
import com.aakriti.notesnative.ui.screens.NotesHomeScreen
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun App() {
  val ctx = LocalContext.current
  val db = remember {
    Room.databaseBuilder(ctx, NotesDb::class.java, "notes.db").build()
  }
  val repo = remember { NotesRepository(db.noteDao()) }
  val settings = remember { SettingsStore(ctx.applicationContext) }

  val vm: NotesViewModel =
    viewModel(factory = NotesViewModelFactory(repo, settings))

  val nav = rememberNavController()

  Surface(modifier = Modifier.fillMaxSize()) {
    NavHost(
      navController = nav,
      startDestination = "home",
    ) {
      composable(
        route = "home",
        exitTransition = {
          slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          )
        },
        popEnterTransition = {
          slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          )
        },
      ) {
        NotesHomeScreen(
          vm = vm,
          onOpenNote = { id -> nav.navigate("note/$id") },
          onCreateNote = {
            val note = repo.newNote()
            vm.upsert(note)
            nav.navigate("note/${note.id}")
          },
        )
      }

      composable(
        route = "note/{id}",
        arguments = listOf(navArgument("id") { type = NavType.StringType }),
        enterTransition = {
          slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          )
        },
        popExitTransition = {
          slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          )
        },
      ) { backStack ->
        val id = backStack.arguments?.getString("id").orEmpty()
        NoteEditorScreen(
          vm = vm,
          noteId = id,
          onBack = { nav.popBackStack() },
        )
      }
    }
  }
}

