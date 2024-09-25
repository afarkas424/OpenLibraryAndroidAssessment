package com.example.openlibraryandroidassessment.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.openlibraryandroidassessment.ui.components.bookDetails.BookDetailsScreen
import com.example.openlibraryandroidassessment.ui.components.books.BooksScreen
import com.example.openlibraryandroidassessment.ui.components.subjects.SubjectsScreen

/**
 * Configures the app's navigation controller
 */
@Composable
fun NavigationSetup(navHostController: NavHostController) {
    // Initially start at subjects screen
    NavHost(navHostController, startDestination = "subjects") {
        composable("subjects") {
            SubjectsScreen(
                navigateToBookScreen = { route:String -> navHostController.navigate(route) }
            )
        }
        composable("books/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""

            // todo: send selected ID to the desired View Model to post to shared flow
            BooksScreen(
                navigateBack = { navHostController.popBackStack() },
                navigateToBookDetails = { route:String -> navHostController.navigate(route) }
            )
        }
        composable("bookDetails/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            // todo: send selected ID to the desired View Model to post to shared flow

            BookDetailsScreen(navHostController)
        }
    }
}