package com.example.openlibraryandroidassessment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.repositories.BookDataRepo
import com.example.openlibraryandroidassessment.ui.navigation.NavigationSetup
import com.example.openlibraryandroidassessment.ui.theme.OpenLibraryAndroidAssessmentTheme
import com.example.openlibraryandroidassessment.viewmodels.OpenLibraryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private var dbHelper: LibraryDatabaseHelper? = null
    private lateinit var libraryViewModel: OpenLibraryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setupDatabase()

        // Todo: the activity doesn't need to store the repo, just pass desired callbacks into the view models
        val bookDataRepo = BookDataRepo(dbHelper!!)
        libraryViewModel = OpenLibraryViewModel(bookDataRepo)

        // with VM created, begin to load subjects in background thread
        CoroutineScope(Dispatchers.IO).launch {
            bookDataRepo.fetchSubjectGroupsAndInsertInLocalDatabase()
        }


        // maybe initially set content to a loading circle or something?
        setContent {
            // instantiate navigation controller
            val navController = rememberNavController()

            OpenLibraryAndroidAssessmentTheme {
                // create navigation graph with navigation controller
                NavigationSetup(navController, libraryViewModel)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper?.close()
    }


    /**
     * Create the database and wipe it to account for prior launches of the app.
     */
    private fun setupDatabase() {
        dbHelper = LibraryDatabaseHelper(applicationContext)
        // clear the database. //todo if database exists, don't wipe or do the read
        dbHelper?.wipeDataBase()
    }

}


