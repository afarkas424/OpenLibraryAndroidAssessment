package com.example.openlibraryandroidassessment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.openlibraryandroidassessment.data.database.LibraryDatabaseHelper
import com.example.openlibraryandroidassessment.data.repositories.BookDataRepo
import com.example.openlibraryandroidassessment.ui.navigation.NavigationSetup
import com.example.openlibraryandroidassessment.ui.theme.OpenLibraryAndroidAssessmentTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    var dbHelper: LibraryDatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch(Dispatchers.IO) {
            // instantiate database with app context
            dbHelper = LibraryDatabaseHelper(applicationContext)

            // start data repo
            val repo = BookDataRepo(dbHelper!!)
            repo.fetchBooksAndCreateDatabase()


        }


        setContent {
            // instantiate navigation controller
            val navController = rememberNavController()

            OpenLibraryAndroidAssessmentTheme {
                // create navigation graph with navigation controller
                NavigationSetup(navController)


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper?.close()
    }

    private fun launch(io: Any, function: () -> Unit) {

    }

    private fun setupDatabase() {

    }
}


